/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.login

//
//  LoginViewModel.kt
//  Tower_Android
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tower_assist.tower_android.data.local.preferences.profile.ProfileRepository
import javax.inject.Inject

/**
 * `ViewModel` for `LoginScreen`.
 *
 * @param profileRepository `ProfileRepository` dependency.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    /**
     * Check if the user has provided all profile information required to make a call.
     *
     * @return Whether the user is allowed to make a call based on the profile information provided.
     */
    suspend fun hasProfile(): Boolean = profileRepository.hasProfile()

}
