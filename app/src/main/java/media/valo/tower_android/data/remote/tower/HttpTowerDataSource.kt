/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.tower

//
//  HttpTowerDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
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
    private val settingsRepository: SettingsRepository
) : TowerDataSource {

    override suspend fun index() {
        request()
    }

    private suspend fun request(
        httpMethod: HttpMethod = HttpMethod.Get,
        path: String = "/"
    ): HttpResponse =
        httpClient.request(settingsRepository.getApiEndpoint() + path) { method = httpMethod }

}
