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
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFWriter

class JenaDTKGEngineTest : StringSpec({
    val digitalTwin = DTKnowledgeGraph(
        "http://example.com/dt",
        listOf(
            Property("https://healthcareontology.com/ontology#isBusy") to Literal(true),
            Property("https://healthcareontology.com/ontology#hasFuelLevel") to Literal(37.0),
            Property("https://smartcityontology.com/ontology#isApproaching") to
                Individual("intersection"),
        ),
    )

    val jenaDigitalTwinKnowledgeGraph = ModelFactory.createDefaultModel().apply {
        this.createResource("http://example.com/dt")
            .addLiteral(this.createProperty("https://healthcareontology.com/ontology#isBusy"), true)
            .addLiteral(this.createProperty("https://healthcareontology.com/ontology#hasFuelLevel"), 37.0)
            .addProperty(
                this.createProperty("https://smartcityontology.com/ontology#isApproaching"),
                this.createResource("intersection"),
            )
    }

    "the current dtkg should be updated correctly after a shadowing event" {
        val dtkgEngine = JenaDTKGEngine()
        dtkgEngine.updateDigitalTwinKnowledgeGraph(UpdateEvent(digitalTwin))
        dtkgEngine.currentDigitalTwinKnowledgeGraph() shouldNotBe null
        dtkgEngine.currentDigitalTwinKnowledgeGraph()?.let {
            it shouldBe RDFWriter.create().lang(Lang.TTL).source(jenaDigitalTwinKnowledgeGraph).asString()
        }
    }
})
