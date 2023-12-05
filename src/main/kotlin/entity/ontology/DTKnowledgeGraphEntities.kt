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

package entity.ontology

/**
 * It models the concept of RDF node in the context of a Digital Twin Knowledge Graph.
 * A Node can be anything in the Knowledge Graph from RDF Resources to Literals.
 */
sealed interface Node

/**
 * It models the concept of RDF Literal.
 * A literal has a [value] of type [T].
 */
data class Literal<T : Any>(val value: T) : Node

/**
 * It models an RDF resource in the context of a Digital Twin Knowledge Graph.
 * So it can be an Individual, a Property or a Blank Node.
 */
sealed interface Resource : Node {
    /** The URI of the Resource. */
    val uri: String?
}

/**
 * It models the concept of Individual in the context of Digital Twin Knowledge Graph.
 * So an individual here is another Digital Twin, identified by its URI.
 */
data class Individual(override val uri: String) : Resource

/**
 * It models the concept of RDF Property in the context of Digital Twin Knowledge Graph.
 */
data class Property(override val uri: String) : Resource

/**
 * It models the concept of RDF Blank Node in the context of Digital Twin Knowledge Graph.
 * A Blank Node could have an associated list of [predicates].
 */
data class BlankNode(val predicates: List<Pair<Property, Node>> = listOf()) : Resource {
    override val uri = null

    /**
     * Add a [predicate] to the [BlankNode].
     * Note that this is an immutable data structure, so it returns a new [BlankNode].
     */
    fun addPredicate(predicate: Pair<Property, Node>): BlankNode = this.copy(predicates = predicates + predicate)
}

/**
 * It models the Digital Twin Knowledge graph of a Digital Twin, identified by its [dtUri], in an abstract way.
 * Its objective is to serve as the base to represent semantic data, in particular its [predicates],
 * about a Digital Twin and not to be a generic implementation of a Knowledge Graph.
 * Its main usage will be as a DTO to be converted in different KG models of different libraries.
 */
data class DTKnowledgeGraph(val dtUri: String, val predicates: List<Pair<Property, Node>> = listOf()) {
    /**
     * Add a [predicate] to the [DTKnowledgeGraph].
     * Note that this is an immutable data structure, so it returns a new [BlankNode].
     */
    fun addPredicate(predicate: Pair<Property, Node>): DTKnowledgeGraph = this.copy(predicates = predicates + predicate)
}
