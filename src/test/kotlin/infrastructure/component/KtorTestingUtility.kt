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
import infrastructure.component.testdouble.WoTDTDManagerTestDouble
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.server.websocket.WebSockets

object KtorTestingUtility {
    fun apiTestApplication(tests: suspend ApplicationTestBuilder.(dtkgEngine: DTKGEngine) -> Unit) {
        val dtkgEngine = JenaDTKGEngine()
        testApplication {
            install(WebSockets)
            application {
                wodtDigitalTwinInterfaceAPI(dtkgEngine, WoTDTDManagerTestDouble())
            }
            tests(dtkgEngine)
        }
    }
}
