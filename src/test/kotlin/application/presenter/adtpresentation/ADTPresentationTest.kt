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

package application.presenter.adtpresentation

import TestingUtils.readResourceFile
import entity.events.DeleteEvent
import entity.events.UpdateEvent
import entity.ontology.DTKnowledgeGraph
import entity.ontology.Individual
import entity.ontology.Literal
import entity.ontology.Property
import entity.ontology.ambulance.AmbulanceOntology
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

class ADTPresentationTest : StringSpec({
    val digitalTwinDelete = DigitalTwinUpdate(
        "entity/ontology/ambulance",
        DigitalTwinEventType.DELETE,
        "2023-02-02T10:00:00",
    )

    val digitalTwinUpdate = DigitalTwinUpdate(
        "entity/ontology/ambulance",
        DigitalTwinEventType.UPDATE,
        "2023-02-02T10:00:00",
        mapOf(
            "busy" to JsonPrimitive(true),
            "fuelLevel" to JsonPrimitive(85.5),
        ),
        listOf(
            DigitalTwinRelationship(
                "entity/ontology/ambulance",
                "rel_is_part_of_mission",
                "mission",
            ),
            DigitalTwinRelationship(
                "entity/ontology/ambulance",
                "rel_is_approaching",
                "intersection",
            ),
        ),
    )

    val convertedDigitalTwin = DTKnowledgeGraph(
        "uri",
        listOf(
            Property("https://healthcareontology.com/ontology#isBusy") to Literal(true),
            Property("https://healthcareontology.com/ontology#hasFuelLevel") to Literal(85.5),
            Property("https://healthcareontology.com/ontology#isInMission") to Individual("mission"),
            Property("https://smartcityontology.com/ontology#isApproaching") to Individual("intersection"),
        ),
    )

    "A simple Digital twin delete event should be deserialized" {
        readResourceFile("emptyDelete.json")?.let {
            shouldNotThrow<Exception> {
                val event = Json.decodeFromString<DigitalTwinUpdate>(it)
                event shouldNotBe null
                event.eventType shouldBe DigitalTwinEventType.DELETE
            }
        }
    }

    "A Digital twin update event with status composed by only properties should be deserialized" {
        readResourceFile("basic.json")?.let {
            shouldNotThrow<Exception> {
                val event = Json.decodeFromString<DigitalTwinUpdate>(it)
                event shouldNotBe null
                event.eventType shouldBe DigitalTwinEventType.UPDATE
                event.properties.size shouldNotBe 0
            }
        }
    }

    "A Digital twin update event with status composed by properties and relationships should be deserialized" {
        readResourceFile("withRelationships.json")?.let {
            shouldNotThrow<Exception> {
                val event = Json.decodeFromString<DigitalTwinUpdate>(it)
                event shouldNotBe null
                event.eventType shouldBe DigitalTwinEventType.UPDATE
                event.properties.size shouldNotBe 0
                event.relationships.size shouldNotBe 0
            }
        }
    }

    "We should be able to extract the DT Knowledge Graph from a Digital Twin Update" {
        digitalTwinUpdate.extractDTKnowledgeGraph("uri", AmbulanceOntology()) shouldBe convertedDigitalTwin
    }

    "A Digital Twin Delete event should be converted in an empty DT Knowledge Graph" {
        digitalTwinDelete.extractDTKnowledgeGraph("uri", AmbulanceOntology()) shouldBe
            DTKnowledgeGraph("uri", listOf())
    }

    "A Digital Twin Update event should be converted in a Update Shadowing event" {
        digitalTwinUpdate.toShadowingEvent("uri", AmbulanceOntology()) shouldBe
            UpdateEvent(digitalTwinUpdate.extractDTKnowledgeGraph("uri", AmbulanceOntology()))
    }

    "A Digital Twin Delete event should be converted in a Delete Shadowing event" {
        digitalTwinDelete.toShadowingEvent("uri", AmbulanceOntology()) shouldBe DeleteEvent
    }
})
