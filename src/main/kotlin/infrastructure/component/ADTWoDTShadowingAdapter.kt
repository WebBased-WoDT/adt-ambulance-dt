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

import application.component.WoDTShadowingAdapter
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import entity.events.ShadowingEvents
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * The [WoDTShadowingAdapter] for Azure Digital Twins.
 * It consumes events from Azure SignalR.
 */
class ADTWoDTShadowingAdapter : WoDTShadowingAdapter {

    init {
        checkNotNull(System.getenv(SIGNALR_NEGOTIATION_URL)) { "Please provide a valid negotiation url" }
        checkNotNull(System.getenv(SIGNALR_TOPIC_NAME)) { "Please provide a valid SignalR topic name" }
    }

    private val _events = MutableSharedFlow<ShadowingEvents>()
    private val signalRConnection = HubConnectionBuilder.create(System.getenv(SIGNALR_NEGOTIATION_URL)).build()

    override val events = _events.asSharedFlow()

    override suspend fun startShadowAdaptation() {
        signalRConnection.on(System.getenv(SIGNALR_TOPIC_NAME), {
            println(it)
        }, String::class.java)
        signalRConnection.persistentStart()
    }

    private fun HubConnection.persistentStart() {
        this.start().blockingAwait()
        logger.info { "Started" }
        this.onClosed {
            if (this.connectionState == HubConnectionState.DISCONNECTED) {
                this.persistentStart()
            }
        }
    }

    companion object {
        private const val SIGNALR_NEGOTIATION_URL = "SIGNALR_NEGOTIATION_URL"
        private const val SIGNALR_TOPIC_NAME = "SIGNALR_TOPIC_NAME"
        private val logger = KotlinLogging.logger {}
    }
}
