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

import entity.events.UpdateEvent
import entity.ontology.DTKnowledgeGraph
import entity.ontology.Individual
import entity.ontology.Literal
import entity.ontology.Property
import entity.ontology.WoDTVocabulary
import infrastructure.component.KtorTestingUtility.apiTestApplication
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class KtorWoDTDigitalTwinInterfaceTest : StringSpec({
    """
        A HTTP GET request on the Digital Twin URI should return a 303 See Other status code with the Location HTTP
        Header set to the Digital Twin Knowledge Graph URL, following COOL URI
    """ {
        apiTestApplication {
            val clientNoRedirect = createClient {
                followRedirects = false
            }
            val response = clientNoRedirect.get("")
            response shouldHaveStatus HttpStatusCode.SeeOther
            response.headers[HttpHeaders.Location] shouldBe "/dtkg"
        }
    }

    "A HTTP GET request on the Digital Twin Knowledge Graph URL should return the HTTP status NoContent if empty" {
        apiTestApplication {
            val response = client.get("/dtkg")
            response shouldHaveStatus HttpStatusCode.NoContent
            response.headers[HttpHeaders.Link] shouldBe "</dtd>; rel=\"${WoDTVocabulary.HAS_DESCRIPTOR}\""
            response.bodyAsText().isEmpty() shouldBe true
        }
    }

    "A HTTP GET request on existent Digital Twin Knowledge Graph should correctly return the current DTKG" {
        val digitalTwin = DTKnowledgeGraph(
            "http://example.com/dt",
            listOf(
                Property("https://healthcareontology.com/ontology#isBusy") to Literal(true),
                Property("https://healthcareontology.com/ontology#hasFuelLevel") to Literal(37.0),
                Property("https://smartcityontology.com/ontology#isApproaching") to
                    Individual("intersection"),
            ),
        )
        apiTestApplication {
            it.updateDigitalTwinKnowledgeGraph(UpdateEvent(digitalTwin))
            val response = client.get("/dtkg")
            response shouldHaveStatus HttpStatusCode.OK
            response.headers[HttpHeaders.Link] shouldBe "</dtd>; rel=\"${WoDTVocabulary.HAS_DESCRIPTOR}\""
            response.bodyAsText() shouldBe it.currentDigitalTwinKnowledgeGraph()
        }
    }

    "A HTTP GET request on the Digital Twin Descriptor should respect the specification" {
        apiTestApplication {
            val response = client.get("/dtd")
            response shouldHaveStatus HttpStatusCode.OK
            response.headers[HttpHeaders.Link] shouldBe "</dtkg>; rel=\"${WoDTVocabulary.CURRENT_STATUS}\""
            response.bodyAsText().isNotEmpty() shouldBe true
        }
    }
})
