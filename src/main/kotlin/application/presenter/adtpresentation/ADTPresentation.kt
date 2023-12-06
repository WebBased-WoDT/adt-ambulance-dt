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

import entity.events.DeleteEvent
import entity.events.ShadowingEvent
import entity.events.UpdateEvent
import entity.ontology.DTKnowledgeGraph
import entity.ontology.DTOntology
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull

/**
 * It models the event received from Azure SignalR.
 * It contains:
 * - [dtId]
 * - [eventType]
 * - [eventDateTime]
 * - a list of the current [properties]
 * - a list of the current [relationships].
 */
@Serializable
data class DigitalTwinUpdate(
    val dtId: String,
    val eventType: DigitalTwinEventType,
    val eventDateTime: String,
    val properties: Map<String, JsonPrimitive> = mapOf(),
    val relationships: List<DigitalTwinRelationship> = listOf(),
)

/**
 * It models a relationship returned in the event from SignalR.
 * Each relationship has a [sourceId] associated with a [targetId] by a [relationshipName].
 */
@Serializable
data class DigitalTwinRelationship(
    @SerialName("\$sourceId")
    val sourceId: String,
    @SerialName("\$relationshipName")
    val relationshipName: String,
    @SerialName("\$targetId")
    val targetId: String,
)

/** It models the Digital Twins event type. */
@Serializable
enum class DigitalTwinEventType {
    /** Update Digital Twin event type. */
    UPDATE,

    /** Delete Digital Twin event type. */
    DELETE,
}

/**
 * It extracts from a [DigitalTwinUpdate] the [DTKnowledgeGraph]. The [DTKnowledgeGraph] is built based on the provided
 * [dtUri] and [ontology].
 */
fun DigitalTwinUpdate.extractDTKnowledgeGraph(dtUri: String, ontology: DTOntology): DTKnowledgeGraph = DTKnowledgeGraph(
    dtUri,
    this.properties.mapNotNull { (rawProperty, jsonValue) ->
        ontology.convertPropertyValue(rawProperty, jsonValue.toPrimitive())
    } + this.relationships.mapNotNull { (_, rawRelationship, target) ->
        ontology.convertRelationship(rawRelationship, target)
    },
)

/**
 * It converts a [DigitalTwinUpdate] to a [ShadowingEvent]. The [ShadowingEvent] is built based on the provided
 * [dtUri] and [ontology].
 */
fun DigitalTwinUpdate.toShadowingEvent(dtUri: String, ontology: DTOntology): ShadowingEvent = when (this.eventType) {
    DigitalTwinEventType.UPDATE -> UpdateEvent(this.extractDTKnowledgeGraph(dtUri, ontology))
    DigitalTwinEventType.DELETE -> DeleteEvent
}

private fun JsonPrimitive.toPrimitive(): Any = when {
    this.isString -> this.content
    this.booleanOrNull != null -> this.boolean
    this.doubleOrNull != null -> this.double
    this.floatOrNull != null -> this.float
    this.intOrNull != null -> this.int
    else -> throw IllegalArgumentException("It is not a primitive")
}
