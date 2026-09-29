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

package de.tower_assist.tower_android.ui.routes.loading

import kotlinx.coroutines.runBlocking
import de.tower_assist.tower_android.FakeCredentialDataSource
import de.tower_assist.tower_android.FakeProfileDataSource
import de.tower_assist.tower_android.FakeSettingsDataSource
import de.tower_assist.tower_android.FakeTowerDataSource
import de.tower_assist.tower_android.data.local.preferences.credentials.CredentialRepository
import de.tower_assist.tower_android.data.local.preferences.profile.ProfileRepository
import de.tower_assist.tower_android.data.local.preferences.settings.SettingsRepository
import de.tower_assist.tower_android.data.remote.tower.TowerRepository
import de.tower_assist.tower_android.model.OpeningHours
import de.tower_assist.tower_android.model.Status
import de.tower_assist.tower_android.model.dummyIndexResponse
import de.tower_assist.tower_android.ui.routes.news.newestNewsVersion
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
