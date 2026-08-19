/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

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
