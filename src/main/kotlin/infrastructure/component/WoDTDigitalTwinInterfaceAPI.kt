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

import application.component.DTDManagerReader
import application.component.DTKGReader
import entity.ontology.WoDTVocabulary
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.async

/**
 * The WoDT Digital Twin Interface API available to handle requests.
 */
fun Application.wodtDigitalTwinInterfaceAPI(dtkgReader: DTKGReader, dtdReader: DTDManagerReader) {
    routing {
        getDigitalTwin()
        getDigitalTwinKnowledgeGraph(dtkgReader)
        getDigitalTwinDescriptor(dtdReader)
    }
}

private fun Route.getDigitalTwin() =
    get("/") {
        call.response.headers.append(HttpHeaders.Location, "/dtkg")
        call.respond(HttpStatusCode.SeeOther)
    }

private fun Route.getDigitalTwinKnowledgeGraph(dtkgReader: DTKGReader) =
    get("/dtkg") {
        async { dtkgReader.currentDigitalTwinKnowledgeGraph() }.await().apply {
            when (this) {
                null -> call.respond(HttpStatusCode.NoContent)
                else -> {
                    call.response.status(HttpStatusCode.OK)
                    call.response.headers.append(HttpHeaders.ContentType, "text/turtle")
                    call.response.headers.append(
                        HttpHeaders.Link,
                        "</dtd>; rel=\"${WoDTVocabulary.HAS_DESCRIPTOR}\"",
                    )
                    call.respond(this)
                }
            }
        }
    }

private fun Route.getDigitalTwinDescriptor(dtdReader: DTDManagerReader) =
    get("/dtd") {
        async { dtdReader.getDTD() }.await().apply {
            when (this) {
                null -> call.respond(HttpStatusCode.NoContent)
                else -> {
                    call.response.status(HttpStatusCode.OK)
                    call.response.headers.append(HttpHeaders.ContentType, "application/td+json")
                    call.response.headers.append(
                        HttpHeaders.Link,
                        "</dtkg>; rel=\"${WoDTVocabulary.CURRENT_STATUS}\"",
                    )
                    call.respond(this.toJson())
                }
            }
        }
    }
