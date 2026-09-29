/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.loading

import kotlinx.coroutines.runBlocking
import media.valo.tower_android.FakeCredentialDataSource
import media.valo.tower_android.FakeProfileDataSource
import media.valo.tower_android.FakeSettingsDataSource
import media.valo.tower_android.FakeTowerDataSource
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.model.OpeningHours
import media.valo.tower_android.model.Status
import media.valo.tower_android.model.dummyIndexResponse
import media.valo.tower_android.ui.routes.news.newestNewsVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

//
//  LoadingViewModelTest.kt
//  Tower_Android
//

private const val SCHEDULE = "Montag bis Freitag von 8 bis 12 und von 13 bis 17 Uhr."
private const val OLDER_VERSION = "0.0.1"

/**
 * Tests for the startup checks in `LoadingViewModel`.
 */
class LoadingViewModelTest {

    private val towerDataSource = FakeTowerDataSource()
    private val profileDataSource = FakeProfileDataSource()
    private val settingsRepository = SettingsRepository(FakeSettingsDataSource())

    /**
     * Build a `LoadingViewModel` on top of the fakes.
     *
     * A fresh `ViewModel` is built per call, so that a test can start the app more than once.
     */
    private fun viewModel() = LoadingViewModel(
        towerRepository = TowerRepository(towerDataSource),
        credentialRepository = CredentialRepository(FakeCredentialDataSource()),
        profileRepository = ProfileRepository(profileDataSource),
        settingsRepository = settingsRepository
    )

    @Test
    fun `goes to the news when no version has been recorded yet`() = runBlocking {
        assertEquals(StartupDestination.News, viewModel().resolveStartupDestination())
    }

    @Test
    fun `goes to the news when the recorded version is not the newest`() = runBlocking {
        settingsRepository.setLastSeenNewsVersion(OLDER_VERSION)

        assertEquals(StartupDestination.News, viewModel().resolveStartupDestination())
    }

    @Test
    fun `goes home when the newest release notes have already been seen`() = runBlocking {
        settingsRepository.setLastSeenNewsVersion(newestNewsVersion)

        assertEquals(StartupDestination.Home, viewModel().resolveStartupDestination())
    }

    @Test
    fun `records the news as seen and does not show them again`() = runBlocking {
        assertEquals(StartupDestination.News, viewModel().resolveStartupDestination())
        assertEquals(newestNewsVersion, settingsRepository.getLastSeenNewsVersion())

        assertEquals(StartupDestination.Home, viewModel().resolveStartupDestination())
    }

    @Test
    fun `goes to the login screen when the backend cannot be reached`() = runBlocking {
        towerDataSource.indexResponse = null

        assertEquals(StartupDestination.Login, viewModel().resolveStartupDestination())
    }

    @Test
    fun `goes to the login screen when the user has no profile`() = runBlocking {
        profileDataSource.setFirstName(null)

        assertEquals(StartupDestination.Login, viewModel().resolveStartupDestination())
    }

    @Test
    fun `goes to the closed screen with the schedule when the service is closed`() = runBlocking {
        towerDataSource.indexResponse = dummyIndexResponse.copy(
            openingHours = OpeningHours(status = Status.CLOSED, description = SCHEDULE)
        )

        assertEquals(
            StartupDestination.Closed(schedule = SCHEDULE),
            viewModel().resolveStartupDestination()
        )
    }

    @Test
    fun `goes to the outdated screen when the backend moved to a new major version`() =
        runBlocking {
            towerDataSource.indexResponse = dummyIndexResponse.copy(apiVersion = "99.0")

            assertEquals(StartupDestination.Outdated, viewModel().resolveStartupDestination())
        }

    @Test
    fun `still shows the news after a start that was turned away`() = runBlocking {
        towerDataSource.indexResponse = null
        assertEquals(StartupDestination.Login, viewModel().resolveStartupDestination())
        assertNull(settingsRepository.getLastSeenNewsVersion())

        towerDataSource.indexResponse = dummyIndexResponse
        assertEquals(StartupDestination.News, viewModel().resolveStartupDestination())
    }

}
