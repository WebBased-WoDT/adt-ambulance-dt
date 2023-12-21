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
import application.component.PlatformManagementInterface
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.delete
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.coroutineScope
import java.util.Collections
import kotlin.collections.LinkedHashSet

/**
 * Base implementation of the [PlatformManagementInterface].
 */
class BasePlatformManagementInterface(
    private val dtdManager: DTDManager,
    customDtUri: String? = null,
) : PlatformManagementInterface {
    private val digitalTwinUri: String

    init {
        if (customDtUri == null) {
            checkNotNull(System.getenv(DIGITAL_TWIN_URI)) { "Please provide the exposed Digital Twin URI" }
        }
        digitalTwinUri = customDtUri ?: System.getenv(DIGITAL_TWIN_URI)
    }

    private val platformSet = Collections.synchronizedSet<String>(LinkedHashSet())

    override suspend fun addPlatform(platformUrl: String) {
        if (platformSet.add(platformUrl)) {
            dtdManager.addPlatform(platformUrl)
        }
    }

    override suspend fun signalDigitalTwinDeletion() {
        val httpClient = HttpClient(CIO)
        val dtUri = digitalTwinUri
        coroutineScope {
            platformSet.forEach {
                httpClient.delete(it) {
                    url {
                        appendPathSegments(dtUri)
                    }
                }
            }
        }
        httpClient.close()
    }

    companion object {
        private const val DIGITAL_TWIN_URI = "DIGITAL_TWIN_URI"
    }
}
