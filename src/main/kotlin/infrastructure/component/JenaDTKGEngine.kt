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

import application.component.DTKGEngine
import entity.events.ShadowingEvent
import entity.events.UpdateEvent
import entity.ontology.BlankNode
import entity.ontology.Individual
import entity.ontology.Literal
import entity.ontology.Node
import entity.ontology.Property
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Resource
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFWriter
import org.apache.jena.shared.Lock

/**
 * This class provides an implementation of the [DTKGEngine] using Apache Jena.
 */
class JenaDTKGEngine(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) : DTKGEngine {
    private val _digitalTwinKnowledgeGraphs = MutableSharedFlow<String>()
    private val model = ModelFactory.createDefaultModel()

    override val digitalTwinKnowledgeGraphs: Flow<String> = _digitalTwinKnowledgeGraphs.asSharedFlow()

    override fun currentDigitalTwinKnowledgeGraph(): String? = with(model) {
        this.enterCriticalSection(Lock.READ)
        this.toTurtle()
    }.also { model.leaveCriticalSection() }

    override fun updateDigitalTwinKnowledgeGraph(shadowingEvent: ShadowingEvent) {
        model.enterCriticalSection(Lock.WRITE)
        model.removeAll()
        if (shadowingEvent is UpdateEvent) {
            model.createResource(shadowingEvent.dtKnowledgeGraph.dtUri)
                .addProperties(model, shadowingEvent.dtKnowledgeGraph.predicates)
        }
        CoroutineScope(dispatcher).launch { _digitalTwinKnowledgeGraphs.emit(model.toTurtle()) }
        model.leaveCriticalSection()
    }

    private fun Resource.addProperties(model: Model, predicates: List<Pair<Property, Node>>): Resource =
        predicates.fold(this) { resource, (property, node) ->
            when (node) {
                is Individual ->
                    resource.addProperty(model.createProperty(property.uri), model.createResource(node.uri))
                is Property -> resource.addProperty(model.createProperty(property.uri), model.createProperty(node.uri))
                is BlankNode -> resource.addProperty(
                    model.createProperty(property.uri),
                    model.createResource().addProperties(model, node.predicates),
                )
                is Literal<*> -> resource.addLiteral(model.createProperty(property.uri), node.value)
            }
        }

    private fun Model.toTurtle() = RDFWriter.create().lang(Lang.TTL).source(this).asString()
}
