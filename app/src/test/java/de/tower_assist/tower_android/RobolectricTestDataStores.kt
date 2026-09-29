/******************************************************************************
 * Copyright (c) 2026 valo.media GmbH                                         *
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

package de.tower_assist.tower_android

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import java.io.File
import kotlinx.coroutines.CoroutineScope

//
//  RobolectricTestDataStores.kt
//  Tower_Android
//

fun createTemporaryPreferencesDataStore(
    context: Context,
    scope: CoroutineScope,
    name: String
): DataStore<Preferences> {
    val dataStoreFile = File(
        context.filesDir,
        "datastore/${name}_${System.nanoTime()}.preferences_pb"
    )
    dataStoreFile.parentFile?.mkdirs()

    return PreferenceDataStoreFactory.create(scope = scope) {
        dataStoreFile
    }
}
