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

package infrastructure.component.testdouble

import TestingUtils.readResourceFile
import application.component.DTDManager
import application.presenter.adtpresentation.ADTModelPresentation.toThingBaseThingDescription
import entity.ontology.WoDTVocabulary
import entity.ontology.ambulance.AmbulanceOntology
import io.github.sanecity.wot.thing.Thing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * Simple test double to use in the tests.
 */
class WoTDTDManagerTestDouble : DTDManager {
    private var platformSet: Set<String> = setOf()

    override suspend fun addPlatform(platformUrl: String): Boolean {
        platformSet = platformSet + platformUrl
        return true
    }

    override suspend fun getDTD(): Thing<*, *, *>? = readResourceFile("ambulanceDTDLmodel.json")?.let {
        Json.decodeFromString<JsonObject>(it).toThingBaseThingDescription("uri", AmbulanceOntology())
            .setMetadata(
                mapOf(
                    "links" to platformSet.map { platform ->
                        object {
                            val href = platform
                            val rel = WoDTVocabulary.REGISTERED_TO_PLATFORM
                        }
                    },
                ),
            )
    }
}
