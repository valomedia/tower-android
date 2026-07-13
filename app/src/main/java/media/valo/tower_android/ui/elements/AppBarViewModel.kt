/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.elements

//
//  AppBarViewModel.kt
//  Tower_Android
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import javax.inject.Inject

/**
 * `ViewModel` for `AppBar`.
 *
 * @param credentialRepository  `CredentialRepository` dependency.
 * @param profileRepository     `ProfileRepository` dependency.
 */
@HiltViewModel
class AppBarViewModel @Inject constructor(
    private val credentialRepository: CredentialRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    /**
     * A Flow that emits the user id every time it is updated.
     *
     * This will emit the new user id each time the user id is set. When the user id is unset, it
     * will emil `null`.
     */
    val userIdFlow: Flow<String?> = credentialRepository.userIdFlow

    /**
     * Log out the user.
     *
     * This will clear the credentials and profile.
     */
    suspend fun logout() {
        credentialRepository.setUserId(null)
        profileRepository.setUserProfile(null)
    }

}
