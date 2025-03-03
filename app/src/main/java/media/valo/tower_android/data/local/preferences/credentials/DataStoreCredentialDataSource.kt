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
import kotlinx.coroutines.flow.map
import media.valo.tower_android.model.Credential
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

    private val usernameKey: Preferences.Key<String> = stringPreferencesKey("username")

    override val usernameFlow: Flow<String?> = dataStore.get(usernameKey)

    override suspend fun setUsername(username: String?) = dataStore.set(usernameKey, username)

    private val passwordKey: Preferences.Key<String> = stringPreferencesKey("password")

    override val passwordFlow: Flow<String?> = dataStore.get(passwordKey)

    override suspend fun setPassword(password: String?) = dataStore.set(passwordKey, password)

    override val credentialFlow: Flow<Credential?> = dataStore.data.map { preferences ->
        preferences[usernameKey]?.let { username ->
            preferences[passwordKey]?.let { password ->
                Credential(username = username, password = password)
            }
        }
    }

    override suspend fun setCredential(credential: Credential?) {
        setUsername(credential?.username)
        setPassword(credential?.password)
    }

}
