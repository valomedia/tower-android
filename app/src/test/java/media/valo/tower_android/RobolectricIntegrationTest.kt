/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import media.valo.tower_android.data.local.preferences.credentials.DataStoreCredentialDataSource
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  RobolectricIntegrationTest.kt
//  Tower_Android
//
//  Created by:
//      * Yatsar (Agent)
//

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class RobolectricIntegrationTest {

    private val dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun `loads application resources and manifest metadata`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_ACTIVITIES or PackageManager.GET_PERMISSIONS
        )

        assertEquals("Tower Fernassistenz", context.getString(R.string.app_name))
        assertEquals(R.string.app_name, packageInfo.applicationInfo?.labelRes)
        assertTrue(
            packageInfo.activities.orEmpty().any { activityInfo ->
                activityInfo.name == MainActivity::class.java.name && activityInfo.exported
            }
        )
        assertTrue(
            packageInfo.requestedPermissions.orEmpty().contains(android.Manifest.permission.CAMERA)
        )
    }

    @Test
    fun `persists credentials through android data store`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataStoreFile = File(
            context.filesDir,
            "datastore/robolectric_credentials_${System.nanoTime()}.preferences_pb"
        )
        dataStoreFile.parentFile?.mkdirs()

        val dataStore = PreferenceDataStoreFactory.create(scope = dataStoreScope) {
            dataStoreFile
        }
        val dataSource = DataStoreCredentialDataSource(dataStore)

        assertNull(dataSource.userIdFlow.first())

        dataSource.setUserId("user-123")
        assertEquals("user-123", dataSource.userIdFlow.first())

        dataSource.setUserId(null)
        assertNull(dataSource.userIdFlow.first())
    }

}
