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
//  DataStoreSettingsDataSource.kt
//  Tower_Android
//

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import media.valo.tower_android.utils.SettingsDataStore
import media.valo.tower_android.utils.get
import media.valo.tower_android.utils.set
import javax.inject.Inject

/**
 * A `SettingsDataSource` backed by a `DataStore`.
 *
 * @param dataStore `DataStore` dependency.
 */
class DataStoreSettingsDataSource @Inject constructor(
    @SettingsDataStore private val dataStore: DataStore<Preferences>
) : SettingsDataSource {

    private val apiEndpointKey: Preferences.Key<String> = stringPreferencesKey("api_endpoint")

    override val apiEndpointFlow: Flow<String?> = dataStore.get(apiEndpointKey)

    override suspend fun setApiEndpoint(apiEndpoint: String?) =
        dataStore.set(apiEndpointKey, apiEndpoint)

    private val lastSeenNewsVersionKey: Preferences.Key<String> =
        stringPreferencesKey("last_seen_news_version")

    override val lastSeenNewsVersionFlow: Flow<String?> = dataStore.get(lastSeenNewsVersionKey)

    override suspend fun setLastSeenNewsVersion(version: String?) =
        dataStore.set(lastSeenNewsVersionKey, version)

}
