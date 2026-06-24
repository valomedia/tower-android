/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.loading

//
//  DataStoreLoadingDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jan Hofherr
//

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import media.valo.tower_android.utils.LoadingDataStore
import media.valo.tower_android.utils.SettingsDataStore
import media.valo.tower_android.utils.get
import media.valo.tower_android.utils.set
import javax.inject.Inject

/**
 * A `LoadingDataSource` backed by a `DataStore`.
 *
 * @param dataStore `DataStore` dependency.
 */
class DataStoreLoadingDataSource @Inject constructor(
    @LoadingDataStore private val dataStore: DataStore<Preferences>
) : LoadingDataSource {

    private val lastLoginAppVersionKey: Preferences.Key<String> = stringPreferencesKey("lastLoginAppVersion")

    override val lastLoginAppVersionFlow: Flow<String?> = dataStore.get(lastLoginAppVersionKey)

    override suspend fun setLastLoginAppVersion(version: String?) =
        dataStore.set(lastLoginAppVersionKey, version)

}
