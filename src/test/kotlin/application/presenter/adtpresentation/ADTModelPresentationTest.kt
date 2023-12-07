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
import application.presenter.adtpresentation.ADTModelPresentation.toThingBaseThingDescription
import entity.ontology.WoDTVocabulary
import entity.ontology.ambulance.AmbulanceOntology
import io.github.sanecity.wot.DefaultWot
import io.github.sanecity.wot.thing.Context
import io.github.sanecity.wot.thing.Thing
import io.github.sanecity.wot.thing.property.ThingProperty
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class ADTModelPresentationTest : StringSpec({
    fun buildProperty(type: String, domainPredicate: String, addAugmentationMetadata: Boolean = false) =
        ThingProperty.Builder()
            .setObjectType(type)
            .setReadOnly(true)
            .setObservable(true)
            .setOptionalProperties(
                mapOf(
                    WoDTVocabulary.DOMAIN_PREDICATE to domainPredicate,
                ).let {
                    if (addAugmentationMetadata) {
                        it.plus(WoDTVocabulary.AUGMENTED_INTERACTION to false)
                    } else { it }
                },
            )
            .build()

    val expectedExposedThing = DefaultWot().produce(
        Thing.Builder().setId("uri").setObjectContext(Context("https://www.w3.org/2019/wot/td/v1")).build(),
    ).addProperty(
        "busy",
        buildProperty(
            "https://www.w3.org/2001/XMLSchema#boolean",
            "https://healthcareontology.com/ontology#isBusy",
            addAugmentationMetadata = true,
        ),
    ).addProperty(
        "fuelLevel",
        buildProperty(
            "https://www.w3.org/2001/XMLSchema#double",
            "https://healthcareontology.com/ontology#hasFuelLevel",
            addAugmentationMetadata = true,
        ),
    ).addProperty(
        "rel_is_part_of_mission",
        buildProperty(
            "https://healthcareontology.com/ontology#Mission",
            "https://healthcareontology.com/ontology#isInMission",
        ),
    ).addProperty(
        "rel_is_approaching",
        buildProperty(
            "https://smartcityontology.com/ontology#Intersection",
            "https://smartcityontology.com/ontology#isApproaching",
        ),
    )

    infix fun ThingProperty<*>.compareTo(other: ThingProperty<*>) {
        this.objectType shouldBe other.objectType
        this.isReadOnly shouldBe other.isReadOnly
        this.isObservable shouldBe other.isObservable
        this.optionalProperties shouldBe other.optionalProperties
    }

    "it should be possible to generate a basic Thing Description from a DTDL model" {
        readResourceFile("ambulanceDTDLmodel.json")?.let {
            val thingDescription = Json.decodeFromString<JsonObject>(it)
                .toThingBaseThingDescription("uri", AmbulanceOntology())
            thingDescription.id shouldBe expectedExposedThing.id
            thingDescription.properties.entries.zip(expectedExposedThing.properties.entries)
                .forEach { (entry, otherEntry) ->
                    entry.value compareTo otherEntry.value
                }
        }
    }
})
