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

package media.valo.tower_android.ui.routes.loading

//
//  LoadingViewModel.kt
//  Tower_Android
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import media.valo.tower_android.BuildConfig
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.model.IndexResponse
import media.valo.tower_android.model.Status
import media.valo.tower_android.ui.routes.news.newestNewsVersion
import javax.inject.Inject

/**
 * `ViewModel` for `LoadingScreen`.
 *
 * @param towerRepository       `TowerRepository` dependency.
 * @param credentialRepository  `CredentialRepository` dependency.
 * @param profileRepository     `ProfileRepository` dependency.
 * @param settingsRepository    `SettingsRepository` dependency.
 */
@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val towerRepository: TowerRepository,
    private val credentialRepository: CredentialRepository,
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private var indexResponse: IndexResponse? = null

    /**
     * Whether the assistants are currently taking calls.
     *
     * This will be null until connect() is successfully called.
     */
    val isServiceOpen: Boolean?
        get() = when (indexResponse?.openingHours?.status) {
            null -> null
            Status.OPEN -> true
            else -> false
        }

    /**
     * The current opening hours of the service.
     *
     * This will be null until connect() is successfully called.
     */
    val schedule: String?
        get() = indexResponse?.openingHours?.description

    /**
     * The major version of the backend.
     *
     * This will be null until connect() is successfully called and -1 if the version couldn't be parsed.
     */
    val backendMajorVersion: Int?
        get() = if(indexResponse?.apiVersion != null) {
            try {
                indexResponse?.apiVersion?.substringBefore(".")?.toInt()
            } catch (_: Error) {
                -1
            }
        } else { null }

    /**
     * The major version of the app.
     */
    val appMajorVersion
        get() = BuildConfig.VERSION_NAME.substringBefore(".").toInt()

    /**
     * Whether the app needs to be updated.
     *
     * The app needs to be updated if:
     * - the backend major version is greater than the app major version
     * - the backend major version could not be parsed
     *
     * This will be null until connect() is successfully called.
     */
    val isAppUpdateNeeded: Boolean?
        get() = backendMajorVersion?.let { it > appMajorVersion || it == -1 }

    /**
     * Check whether the user has provided all required profile information.
     */
    suspend fun hasProfile(): Boolean = profileRepository.hasProfile()

    /**
     * Ensure the backend can be reached and register for an identity if necessary.
     *
     * @return Whether the connection was successful.
     */
    suspend fun connect(): Boolean {
        try {
            indexResponse = towerRepository.index()
            if (credentialRepository.getUserId().isNullOrBlank()) {
                credentialRepository.setUserId(towerRepository.registerUser().userId)
            }
            return true
        } catch (_: Exception) {
            return false
        }
    }

    /**
     * Run the startup checks and decide which screen to show next.
     *
     * The news are only considered once every gate has been passed, and the version is only
     * recorded then, so that a start turned away at one of the gates still shows the news later.
     *
     * @return The `StartupDestination` to navigate to.
     */
    suspend fun resolveStartupDestination(): StartupDestination {
        val isConnected = connect()
        val serviceOpen = isServiceOpen
        val currentSchedule = schedule
        val updateNeeded = isAppUpdateNeeded

        return when {
            updateNeeded == true -> StartupDestination.Outdated
            !isConnected
                    || !hasProfile()
                    || serviceOpen == null
                    || currentSchedule == null
                    || updateNeeded == null -> StartupDestination.Login
            !serviceOpen -> StartupDestination.Closed(schedule = currentSchedule)
            settingsRepository.getLastSeenNewsVersion() != newestNewsVersion -> {
                settingsRepository.setLastSeenNewsVersion(newestNewsVersion)
                StartupDestination.News
            }
            else -> StartupDestination.Home
        }
    }
}
