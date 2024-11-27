/******************************************************************************
 * Copyright (c) 2024.                                                        *
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
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.remote.tower.TowerRepository
import javax.inject.Inject

/**
 * `ViewModel` for `LoadingScreen`.
 *
 * @param towerRepository       `TowerRepository` dependency.
 * @param credentialRepository  `CredentialRepository` dependency.
 */
@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val towerRepository: TowerRepository,
    private val credentialRepository: CredentialRepository
) : ViewModel() {

    /**
     * Check whether the app currently has any credential (whether valid or not).
     */
    suspend fun hasCredential(): Boolean = credentialRepository.hasCredential()

    /**
     * Check whether the app currently has a valid credential.
     */
    suspend fun checkCredential(): Boolean {
        try {
            towerRepository.index()
            return true
        } catch (_: Exception) {
            return false
        }
    }

}
