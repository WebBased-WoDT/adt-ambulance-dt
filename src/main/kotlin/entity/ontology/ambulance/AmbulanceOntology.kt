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

package entity.ontology.ambulance

import entity.ontology.DTOntology
import entity.ontology.Individual
import entity.ontology.Literal
import entity.ontology.Node
import entity.ontology.Property

/** The ontology used for the Ambulance DT. */
class AmbulanceOntology : DTOntology {
    override fun obtainProperty(rawProperty: String): Property? =
        (propertyMap + relationshipMap)[rawProperty]?.let { Property(it) }

    override fun <T : Any> convertPropertyValue(rawProperty: String, value: T): Pair<Property, Node>? =
        propertyMap[rawProperty]?.let { Property(it) to Literal(value) }

    override fun convertRelationship(rawRelationship: String, targetUri: String): Pair<Property, Node>? =
        relationshipMap[rawRelationship]?.let { Property(it) to Individual(targetUri) }

    companion object {
        private val propertyMap = mapOf(
            "busy" to "https://healthcareontology.com/ontology#isBusy",
            "fuelLevel" to "https://healthcareontology.com/ontology#hasFuelLevel",
        )
        private val relationshipMap = mapOf(
            "rel_is_part_of_mission" to "https://healthcareontology.com/ontology#isInMission",
            "rel_is_approaching" to "https://smartcityontology.com/ontology#isApproaching",
        )
    }
}
