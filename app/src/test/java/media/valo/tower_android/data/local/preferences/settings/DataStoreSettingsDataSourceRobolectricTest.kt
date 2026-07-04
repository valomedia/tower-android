/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.settings

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

//
//  DataStoreSettingsDataSourceRobolectricTest.kt
//  Tower_Android
//
//  Created by:
//      * Yatsar (Agent)
//

private const val DEFAULT_API_ENDPOINT = "https://api.tower-assist.de"
private const val CUSTOM_API_ENDPOINT = "https://api.dev.tower-assist.de"

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class DataStoreSettingsDataSourceRobolectricTest {

    private val dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun `persists api endpoint through android data store`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataStore = createTemporaryPreferencesDataStore(
            context = context,
            scope = dataStoreScope,
            name = "robolectric_settings"
        )
        val dataSource = DataStoreSettingsDataSource(dataStore)

        assertNull(dataSource.apiEndpointFlow.first())

        dataSource.setApiEndpoint(CUSTOM_API_ENDPOINT)
        assertEquals(CUSTOM_API_ENDPOINT, dataSource.apiEndpointFlow.first())

        dataSource.setApiEndpoint(null)
        assertNull(dataSource.apiEndpointFlow.first())
    }

    @Test
    fun `settings repository uses default api endpoint when unset`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataStore = createTemporaryPreferencesDataStore(
            context = context,
            scope = dataStoreScope,
            name = "robolectric_settings_repository"
        )
        val dataSource = DataStoreSettingsDataSource(dataStore)
        val repository = SettingsRepository(dataSource)

        assertEquals(DEFAULT_API_ENDPOINT, repository.getApiEndpoint())

        repository.setApiEndpoint(CUSTOM_API_ENDPOINT)
        assertEquals(CUSTOM_API_ENDPOINT, repository.getApiEndpoint())

        repository.setApiEndpoint(null)
        assertEquals(DEFAULT_API_ENDPOINT, repository.getApiEndpoint())
    }

}
