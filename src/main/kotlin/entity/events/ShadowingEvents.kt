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

package entity.events

import entity.ontology.DTKnowledgeGraph

/** Interface that models the shadowing events to be dispatched through the system. */
sealed interface ShadowingEvents

/** This class models the event of the update of the [dtKnowledgeGraph]. */
data class UpdateEvent(val dtKnowledgeGraph: DTKnowledgeGraph) : ShadowingEvents

/** This class models the event of the deletion of the digital twin. */
data object DeleteEvent : ShadowingEvents
