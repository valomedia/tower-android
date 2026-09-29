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

package de.tower_assist.tower_android.utils

//
//  DataStoreExtensions.kt
//  Tower_Android
//

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Get a flow for a single preference from the `DataStore` by its `Key`.
 *
 * @param key   The `Key` for the preference to get.
 *
 * @return A `Flow` that emits the preference for the `Key` whenever the `DataStore` emits new data.
 */
fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): Flow<T?> =
    this.data.map { preferences -> preferences[key] }

/**
 * Set a new value for a preference in the `DataStore` under a given `Key`.
 *
 * @param key   The `Key` for the preference to set.
 * @param value The value for the preference to set.
 */
suspend fun <T> DataStore<Preferences>.set(key: Preferences.Key<T>, value: T?) {
    this.edit { preferences ->
        if (value == null) {
            preferences.remove(key)
        } else {
            preferences[key] = value
        }
    }
}
