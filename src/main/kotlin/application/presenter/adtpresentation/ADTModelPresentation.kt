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

import entity.ontology.DTOntology
import entity.ontology.WoDTVocabulary
import io.github.sanecity.wot.DefaultWot
import io.github.sanecity.wot.thing.Context
import io.github.sanecity.wot.thing.ExposedThing
import io.github.sanecity.wot.thing.Thing
import io.github.sanecity.wot.thing.property.ThingProperty
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Module that wraps the ADT Model Presentation.
 */
object ADTModelPresentation {
    private const val ELEMENT_TYPE = "@type"
    private const val PROPERTY_TYPE = "Property"
    private const val NAME = "name"
    private const val CONTENTS = "contents"
    private const val THING_DESCRIPTION_CONTEXT = "https://www.w3.org/2019/wot/td/v1"

    /**
     * Convert a DTDL model to a Thing Description.
     */
    fun JsonObject.toThingBaseThingDescription(dtUri: String, ontology: DTOntology): ExposedThing {
        val thing = Thing.Builder()
            .setId(dtUri)
            .setObjectContext(Context(THING_DESCRIPTION_CONTEXT))
            .build()
        val exposedThing = DefaultWot().produce(thing)
        this[CONTENTS]?.jsonArray?.forEach { exposedThing.addDTDLElement(it, ontology) }
        return exposedThing
    }

    private fun ExposedThing.addDTDLElement(element: JsonElement, ontology: DTOntology) {
        val type = element.jsonObject[ELEMENT_TYPE]?.jsonPrimitive?.content
        val name = element.jsonObject[NAME]?.jsonPrimitive?.content.orEmpty()
        this.addProperty(
            name,
            ThingProperty.Builder()
                .setObjectType(ontology.obtainPropertyValueType(name))
                .setReadOnly(true)
                .setObservable(true)
                .setOptionalProperties(
                    mapOf(
                        WoDTVocabulary.DOMAIN_PREDICATE to ontology.obtainProperty(name)?.uri,
                    ).let {
                        if (type == PROPERTY_TYPE) {
                            it.plus(WoDTVocabulary.AUGMENTED_INTERACTION to false)
                        } else { it }
                    },
                )
                .build(),
        )
    }
}
