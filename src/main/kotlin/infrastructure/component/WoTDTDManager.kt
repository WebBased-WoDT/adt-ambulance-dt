/*
 * Copyright (c) 2023. Andrea Giulianelli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package infrastructure.component

import application.component.DTDManager
import application.presenter.adtpresentation.ADTModelPresentation.toThingBaseThingDescription
import com.azure.digitaltwins.core.BasicDigitalTwin
import com.azure.digitaltwins.core.DigitalTwinsClient
import com.azure.digitaltwins.core.DigitalTwinsClientBuilder
import com.azure.digitaltwins.core.implementation.models.ErrorResponseException
import com.azure.identity.DefaultAzureCredentialBuilder
import entity.ontology.DTOntology
import entity.ontology.WoDTVocabulary
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.sanecity.wot.thing.Thing
import io.github.sanecity.wot.thing.Type
import io.github.sanecity.wot.thing.form.Form
import io.github.sanecity.wot.thing.form.Operation
import io.github.sanecity.wot.thing.property.ThingProperty
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * A Digital Twin Descriptor Manager implementation that convert Azure Digital Twins model to WoT Thing Description.
 * It will use the Digital Twin [ontology] and the processes will be dispatched through the [dispatcher].
 */
class WoTDTDManager(
    private val ontology: DTOntology,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DTDManager {
    private var registeredPlatforms: List<String> = listOf()
    private val mutex = Mutex()

    init {
        checkNotNull(System.getenv(ADT_CLIENT_ID)) { "Please provide the Azure client app id" }
        checkNotNull(System.getenv(ADT_TENANT)) { "Please provide the Azure tenant id" }
        checkNotNull(System.getenv(ADT_SECRET)) { "Please provide the Azure client secret id" }
        checkNotNull(System.getenv(ADT_ENDPOINT)) { "Please provide the Azure dt endpoint" }
        checkNotNull(System.getenv(ADT_DT_ID)) { "Please provide the Digital Twin ID on Azure Digital Twins" }
        checkNotNull(System.getenv(PA_ID)) { "Please provide the Physical Asset ID" }
        checkNotNull(System.getenv(DIGITAL_TWIN_URI)) { "Please provide the exposed Digital Twin URI" }
    }

    private val dtClient = DigitalTwinsClientBuilder()
        .credential(DefaultAzureCredentialBuilder().build())
        .endpoint(System.getenv(ADT_ENDPOINT))
        .buildClient()

    override suspend fun addPlatform(platformUrl: String): Boolean {
        mutex.withLock {
            registeredPlatforms = registeredPlatforms + platformUrl
        }
        return true
    }

    override suspend fun getDTD(): Thing<*, *, *>? =
        withContext(dispatcher) {
            dtClient.applySafeDigitalTwinOperation(null) {
                getDigitalTwin(System.getenv(ADT_DT_ID), BasicDigitalTwin::class.java)
            }?.let {
                dtClient.applySafeDigitalTwinOperation(null) {
                    getModel(it.metadata.modelId)
                }
            }?.let { dtModelObject ->
                Json.decodeFromString<JsonObject>(dtModelObject.dtdlModel)
                    .toThingBaseThingDescription(System.getenv(DIGITAL_TWIN_URI), ontology)
                    .setObjectType(Type(ontology.dtType))
                    .addProperty(
                        SNAPSHOT_DTD_PROPERTY,
                        ThingProperty.Builder()
                            .setReadOnly(true)
                            .setObservable(true)
                            .build(),
                    )
                    .setMetadata(buildDTDMetadata())
                    .also {
                        // Necessary considering that the wot-servient library when adding a property
                        // to an ExposedThing resets in an unexpected way the forms.
                        it.getProperty(SNAPSHOT_DTD_PROPERTY)
                            .addForm(
                                Form.Builder()
                                    .addOp(Operation.OBSERVE_PROPERTY)
                                    .setHref("ws://localhost:3000/dtkg")
                                    .setSubprotocol("websocket")
                                    .build(),
                            )
                    }
            }
        }

    private suspend fun buildDTDMetadata() = mapOf(
        WoDTVocabulary.VERSION to VERSION,
        WoDTVocabulary.PHYSICAL_ASSET_ID to System.getenv(PA_ID),
        mutex.withLock {
            "links" to registeredPlatforms.map { platform ->
                object {
                    val href = platform
                    val rel = WoDTVocabulary.REGISTERED_TO_PLATFORM
                }
            }
        },
    )

    private fun <R> DigitalTwinsClient.applySafeDigitalTwinOperation(
        defaultResult: R,
        operation: DigitalTwinsClient.() -> R,
    ): R =
        try {
            operation()
        } catch (exception: ErrorResponseException) {
            logger.error { exception }
            defaultResult
        }

    companion object {
        private const val ADT_CLIENT_ID = "AZURE_CLIENT_ID"
        private const val ADT_TENANT = "AZURE_TENANT_ID"
        private const val ADT_SECRET = "AZURE_CLIENT_SECRET"
        private const val ADT_ENDPOINT = "AZURE_DT_ENDPOINT"
        private const val ADT_DT_ID = "AZURE_DT_ID"
        private const val PA_ID = "PHYSICAL_ASSET_ID"
        private const val DIGITAL_TWIN_URI = "DIGITAL_TWIN_URI"
        private const val VERSION = "1.0.0"
        private const val SNAPSHOT_DTD_PROPERTY = "snapshot"

        private val logger = KotlinLogging.logger {}
    }
}
