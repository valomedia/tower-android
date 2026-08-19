/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
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
