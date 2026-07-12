/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.credentials

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import media.valo.tower_android.createTemporaryPreferencesDataStore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class DataStoreCredentialDataSourceRobolectricTest {

    private val dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun `persists user id through android data store`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataStore = createTemporaryPreferencesDataStore(
            context = context,
            scope = dataStoreScope,
            name = "robolectric_credentials"
        )
        val dataSource = DataStoreCredentialDataSource(dataStore)

        assertNull(dataSource.userIdFlow.first())

        dataSource.setUserId("user-123")
        assertEquals("user-123", dataSource.userIdFlow.first())

        dataSource.setUserId(null)
        assertNull(dataSource.userIdFlow.first())
    }

}
