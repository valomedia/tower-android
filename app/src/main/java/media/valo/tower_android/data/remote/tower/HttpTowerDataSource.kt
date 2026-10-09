/******************************************************************************
 * Copyright (c) 2024-2026 valo.media GmbH                                    *
 * All rights reserved.                                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Affero General Public License as             *
 * published by the Free Software Foundation, either version 3 of the         *
 * License, or (at your option) any later version.                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Affero General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Affero General Public License   *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package media.valo.tower_android.data.remote.tower

//
//  HttpTowerDataSource.kt
//  Tower_Android
//

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.model.AwaitAssistanceResponse
import media.valo.tower_android.model.IndexResponse
import media.valo.tower_android.model.RegisterUserResponse
import media.valo.tower_android.model.RequestAssistanceResponse
import media.valo.tower_android.utils.TowerHttpClient
import javax.inject.Inject

/**
 * A `TowerDataSource` backed by a `HttpClient`.
 *
 * @param httpClient            `HttpClient` dependency.
 * @param settingsRepository    `SettingsRepository` dependency.
 */
class HttpTowerDataSource @Inject constructor(
    @TowerHttpClient private val httpClient: HttpClient,
    private val settingsRepository: SettingsRepository,
    private val credentialRepository: CredentialRepository
) : TowerDataSource {

    override suspend fun index(): IndexResponse =
        get("/").body<IndexResponse>()

    override suspend fun registerUser(): RegisterUserResponse =
        post("/registerUser").body()

    override suspend fun requestAssistance(): RequestAssistanceResponse =
        post("/requestAssistance", mapOf("userId" to credentialRepository.getUserId())).body()

    override suspend fun awaitAssistance(): AwaitAssistanceResponse =
        post("/awaitAssistance", mapOf("userId" to credentialRepository.getUserId())).body()

    override suspend fun cancelAssistance() {
        post("/cancelAssistance", mapOf("userId" to credentialRepository.getUserId()))
    }

    private suspend inline fun get(path: String): HttpResponse = request(path)

    private suspend inline fun post(path: String): HttpResponse = request(path) {
        method = HttpMethod.Post
    }

    private suspend inline fun <reified T> post(path: String, body: T): HttpResponse = request(path) {
        method = HttpMethod.Post
        contentType(ContentType.Application.Json)
        setBody(body)
    }

    private suspend fun request(
        path: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse =
        httpClient.request(settingsRepository.getApiEndpoint() + path, block)

}
