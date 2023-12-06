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

import city.sane.wot.thing.Thing

/**
 * This interface models the reader part of the DTD Manager component.
 */
interface DTDManagerReader {
    /**
     * Obtain the Digital Twin Descriptor.
     * In this Digital Twin it is used a Thing Description.
     */
    fun getDTD(): Thing<*, *, *>
}

/**
 * This interface models the complete DTD Manager component.
 */
interface DTDManager : DTDManagerReader {
    /**
     * Add a WoDT Digital Twins Platform via its [platformUrl].
     */
    fun addPlatform(platformUrl: String): Boolean
}
