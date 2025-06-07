/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.loading

//
//  LoadingViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//      * mvlexs
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import media.valo.tower_android.BuildConfig
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.model.IndexResponse
import media.valo.tower_android.model.Status
import javax.inject.Inject

/**
 * `ViewModel` for `LoadingScreen`.
 *
 * @param towerRepository       `TowerRepository` dependency.
 * @param credentialRepository  `CredentialRepository` dependency.
 * @param profileRepository     `ProfileRepository` dependency.
 */
@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val towerRepository: TowerRepository,
    private val credentialRepository: CredentialRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private var indexResponse: IndexResponse? = null

    /**
     * This holds true, when the backend call succeeds and holds the status message 'open'.
     *
     * If the Backend could not be reached this holds null.
     *
     * Else this holds false, assuming the service is closed
     */
    val isServiceOpen: Boolean?
        get() = when (indexResponse?.openingHours?.status) {
            null -> null
            Status.OPEN -> true
            else -> false
        }

    /**
     * This holds the description retrieved from a successful backend call.
     *
     * The description holds human readable information about the current opening hours.
     */
    val schedule: String?
        get() = indexResponse?.openingHours?.description

    /**
     * This holds null if there was an Issue connecting to the backend.
     *
     * This holds '-1' if there was an Issue parsing the String holding the api Version.
     *
     * Otherwise this holds the backends Major Version
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
     * This parses the Major Version of the app from the Version Name, which is defined in the gradle build config.
     */
    val appMajorVersion
        get() = BuildConfig.VERSION_NAME.substringBefore(".").toInt()

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
}
