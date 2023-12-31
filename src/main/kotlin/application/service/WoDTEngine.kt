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

package application.service

import application.component.DTKGEngine
import application.component.PlatformManagementInterface
import application.component.WoDTShadowingAdapter
import application.component.WoDTWebServer
import entity.events.DeleteEvent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * The Engine that runs the WoDT layer.
 * It takes the [wodtShadowingAdapter] component.
 */
class WoDTEngine(
    private val wodtShadowingAdapter: WoDTShadowingAdapter,
    private val dtkgEngine: DTKGEngine,
    private val platformManagementInterface: PlatformManagementInterface,
    private val wodtWebServer: WoDTWebServer,
) {
    /**
     * Method to start the [WoDTEngine].
     */
    suspend fun start() = coroutineScope {
        wodtShadowingAdapter.startShadowAdaptation()
        launch {
            wodtShadowingAdapter.events.collect {
                dtkgEngine.updateDigitalTwinKnowledgeGraph(it)
                if (it is DeleteEvent) {
                    platformManagementInterface.signalDigitalTwinDeletion()
                }
            }
        }
        wodtWebServer.start()
    }
}
