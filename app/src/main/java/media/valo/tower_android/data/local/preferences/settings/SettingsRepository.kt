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

package media.valo.tower_android.data.local.preferences.settings

//
//  SettingsRepository.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val DEFAULT_API_ENDPOINT = "https://api.tower-assist.de"

/**
 * Repository for application-wide settings.
 *
 * @param settingsDataSource    `SettingsDataSource` dependency.
 */
class SettingsRepository @Inject constructor(
    private val settingsDataSource: SettingsDataSource
) {

    /**
     * A flow that emits the api endpoint for the TOWER api every time it is updated.
     *
     * This will emit the new api endpoint to use for connections to the TOWER backend each time the
     * api endpoint is set. When the api endpoint is unset, it will emit `null`.
     */
    val apiEndpointFlow: Flow<String> = settingsDataSource.apiEndpointFlow.map { apiEndpoint ->
        apiEndpoint ?: DEFAULT_API_ENDPOINT
    }

    /**
     * Get the api endpoint for the TOWER api (if any).
     *
     * @return The api endpoint for connections to the TOWER backend, or `null` if it is unset.
     */
    suspend fun getApiEndpoint(): String = apiEndpointFlow.firstOrNull() ?: DEFAULT_API_ENDPOINT

    /**
     * Change the api endpoint for the TOWER api.
     *
     * This is used to update the api endpoint to use for connections to the TOWER backend (such as
     * when switching between the development and production environments).
     *
     * @param apiEndpoint   The new api endpoint to set, or `null` to unset the api endpoint.
     */
    suspend fun setApiEndpoint(apiEndpoint: String?) =
        settingsDataSource.setApiEndpoint(apiEndpoint)

    /**
     * Get the version whose news the user has seen (if any).
     *
     * @return The recorded version, or `null` if the news have never been shown.
     */
    suspend fun getLastSeenNewsVersion(): String? =
        settingsDataSource.lastSeenNewsVersionFlow.firstOrNull()

    /**
     * Change the version whose news the user has seen.
     *
     * @param version   The version to record, or `null` to unset it.
     */
    suspend fun setLastSeenNewsVersion(version: String?) =
        settingsDataSource.setLastSeenNewsVersion(version)

}
