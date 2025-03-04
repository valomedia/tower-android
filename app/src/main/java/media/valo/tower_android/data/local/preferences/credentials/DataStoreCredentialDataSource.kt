/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.credentials

//
//  DataStoreCredentialDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import media.valo.tower_android.utils.CredentialsDataStore
import media.valo.tower_android.utils.get
import media.valo.tower_android.utils.set
import javax.inject.Inject

/**
 * A `CredentialDataSource` backed by a `DataStore`.
 *
 * @param dataStore `DataStore` dependency.
 */
class DataStoreCredentialDataSource @Inject constructor(
    @CredentialsDataStore private val dataStore: DataStore<Preferences>
) : CredentialDataSource {

    private val userIdKey: Preferences.Key<String> = stringPreferencesKey("userId")

    override val userIdFlow: Flow<String?> = dataStore.get(userIdKey)

    override suspend fun setUserId(userId: String?) = dataStore.set(userIdKey, userId)

}
