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

package de.tower_assist.tower_android.data.local.preferences.credentials

//
//  DataStoreCredentialDataSource.kt
//  Tower_Android
//

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import de.tower_assist.tower_android.utils.CredentialsDataStore
import de.tower_assist.tower_android.utils.get
import de.tower_assist.tower_android.utils.set
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
