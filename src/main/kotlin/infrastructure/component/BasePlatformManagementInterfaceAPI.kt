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

import application.component.PlatformManagementInterface
import application.presenter.api.PlatformRegistration
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

/**
 * The Platform Management Interface API available to WoDT Digital Twins Platform in order to signal
 * the registration of the WoDT Digital Twin to them.
 */
fun Application.platformManagementInterfaceAPI(platformManagementInterface: PlatformManagementInterface) {
    routing {
        registeredToPlatform(platformManagementInterface)
    }
}

private fun Route.registeredToPlatform(platformManagementInterface: PlatformManagementInterface) =
    post("/platform") {
        val platform = call.receive<PlatformRegistration>()
        platformManagementInterface.addPlatform(platform.self)
    }
