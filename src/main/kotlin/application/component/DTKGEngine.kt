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

package application.component

import entity.ontology.DTKnowledgeGraph
import kotlinx.coroutines.flow.Flow

/**
 * This interface models the reader part of the DTKGEngine component.
 */
interface DTKGReader {
    /** Obtain the flow of Digital Twin Knowledge Graphs emitted by the component. */
    val digitalTwinKnowledgeGraphs: Flow<String>

    /** Obtain the current status of the Digital Twin Knowledge Graph. */
    fun currentDigitalTwinKnowledgeGraph(): String?
}

/**
 * This interface models the writer part of the DTKGEngine component.
 */
interface DTKGWriter {
    /** Update the current view over the Digital Twin Knowledge Graph. */
    fun updateDigitalTwinKnowledgeGraph(dtKnowledgeGraph: DTKnowledgeGraph)
}

/**
 * This is the general interface of the DTKGEngine.
 */
interface DTKGEngine : DTKGWriter, DTKGReader
