import application.service.WoDTEngine
import entity.ontology.ambulance.AmbulanceOntology
import infrastructure.component.ADTWoDTShadowingAdapter
import infrastructure.component.BasePlatformManagementInterface
import infrastructure.component.JenaDTKGEngine
import infrastructure.component.KtorWoDTWebServer
import infrastructure.component.WoTDTDManager
import kotlinx.coroutines.runBlocking

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

/**
 * Template for kotlin projects.
 */
fun main(): Unit = runBlocking {
    val ambulanceOntology = AmbulanceOntology()
    val woDTShadowingAdapter = ADTWoDTShadowingAdapter(ambulanceOntology)
    val dtkgEngine = JenaDTKGEngine()
    val dtdManager = WoTDTDManager(ambulanceOntology)
    val platformManagementInterface = BasePlatformManagementInterface(dtdManager)
    val wodtWebServer = KtorWoDTWebServer(dtkgEngine, dtdManager, platformManagementInterface)

    val woDTEngine = WoDTEngine(woDTShadowingAdapter, dtkgEngine, platformManagementInterface, wodtWebServer)

    woDTEngine.start()
}
