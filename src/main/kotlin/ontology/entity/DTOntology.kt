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

package ontology.entity

/**
 * It models the ontology followed by the Digital Twin.
 * This will be used to convert raw data to semantic data, following the domain ontology.
 * This interface is the one that DT Developer must implement.
 */
interface DTOntology {
    /** Obtain the ontology property from the [rawProperty] in input. */
    fun obtainProperty(rawProperty: String): Property

    /** Convert a [rawProperty] and its [value] to the ontology model. */
    fun <T : Any> convertPropertyValue(rawProperty: String, value: T): Pair<Property, Node>

    /** Convert a [rawRelationship] and its [targetUri] to the ontology model. */
    fun convertRelationship(rawRelationship: String, targetUri: String): Pair<Property, Node>
}
