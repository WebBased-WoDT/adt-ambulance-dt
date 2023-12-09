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

import application.presenter.api.PlatformRegistration
import infrastructure.component.KtorTestingUtility.apiTestApplication
import infrastructure.component.testdouble.WoTDTDManagerTestDouble
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class BasePlatformManagementInterfaceTest : StringSpec({
    "When a platform registration is requested to the endpoint, then the Digital Twin Descriptor should be updated" {
        val dtdManager = WoTDTDManagerTestDouble()
        apiTestApplication(dtdManager) { _ ->
            client.post("/platform") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(PlatformRegistration("http://sampleplatform.com")))
            }
            dtdManager.getDTD()?.toJson()?.let {
                Json.decodeFromString<JsonObject>(it)["links"]
                    ?.jsonArray
                    ?.get(0)
                    ?.jsonObject
                    ?.get("href")
                    ?.jsonPrimitive
                    ?.content shouldBe "http://sampleplatform.com"
            }
        }
    }
})
