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

package infrastructure.adtpresentation

import TestingUtils.readResourceFile
import infrastructure.component.adtpresentation.DigitalTwinEventType
import infrastructure.component.adtpresentation.DigitalTwinUpdate
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.Json

class ADTPresentationTest : StringSpec({

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
})
