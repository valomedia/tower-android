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
    @LodaingDataStore private val dataStore: DataStore<Preferences>
) : LoadingDataSource {

    private val lastLoginAppVersionKey: Preferences.Key<String> = stringPreferencesKey("lastLoginAppVersion")

    override val lastLoginAppVersion: String? = dataStore.get(lastLoginAppVersionKey)

    override suspend fun setLastLoginAppVersion(version: String?) =
        dataStore.set(lastLoginAppVersionKey, version)

}
