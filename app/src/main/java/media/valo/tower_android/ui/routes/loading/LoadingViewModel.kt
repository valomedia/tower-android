/******************************************************************************
 * Copyright (c) 2025.                                                        *
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
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.model.IndexResponse
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

    lateinit var indexResponse: IndexResponse
    var isOpen = true
    var scheduleDescription = "Aktuell können die Öffnungszeiten leider nicht geladen werden."

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
            isOpen = indexResponse.openingHours.status == "open"
            scheduleDescription = indexResponse.openingHours.description
            if (credentialRepository.getUserId().isNullOrBlank()) {
                credentialRepository.setUserId(towerRepository.registerUser().userId)
            }
            return true
        } catch (_: Exception) {
            return false
        }
    }

}

