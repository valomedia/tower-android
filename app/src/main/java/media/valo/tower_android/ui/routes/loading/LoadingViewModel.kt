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

    private var backendMajorVersion: Int? = null

    private val appMajorVersion = BuildConfig.VERSION_NAME.substringBefore(".").toInt()

    /**
     * Check whether the user has provided all required profile information.
     */
    suspend fun hasProfile(): Boolean = profileRepository.hasProfile()

    /**
     * Ensure the backend can be reached and register for an identity if necessary.
     *
     * @return Whether the connection was successful.
     */
    suspend fun connect(): IndexResponse? {
        try {
            indexResponse = towerRepository.index()
            if (credentialRepository.getUserId().isNullOrBlank()) {
                credentialRepository.setUserId(towerRepository.registerUser().userId)
            }
            backendMajorVersion = try { indexResponse?.apiVersion?.substringBefore("." )?.toInt() } catch(_:Error) {appMajorVersion + 1}
            return indexResponse
        } catch (_: Exception) {
            return null
        }
    }

    /**
     * This will compare the backend's and app's major version and return true if the app's is smaller than that of the backend.
     */
    val isAppUpdateNeeded: Boolean?
        get() = backendMajorVersion?.let { appMajorVersion < it }

    /**
     * This returns true, when the backend call succeeds and holds the status message 'open'.
     */
    val isServiceOpen
        get() = indexResponse?.openingHours?.status == Status.OPEN

    /**
     * This returns the description retrieved from a successful backend call.
     *
     * The description holds human readable information about the current opening hours.
     */
    val schedule
        get() = indexResponse?.openingHours?.description
}
