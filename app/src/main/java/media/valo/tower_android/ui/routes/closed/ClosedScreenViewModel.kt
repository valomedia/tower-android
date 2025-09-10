/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.screens.closed

//
//  AppBarViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository

/**
 * `ViewModel` for `ClosedScreen`.
 */
@HiltViewModel
class ClosedScreenViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    /** Fast getters for Compose previews or already-known values. */
    val firstNameFlow = profileRepository.firstNameFlow
    val emailFlow     = profileRepository.emailFlow

    /** Suspend functions for “grab it right now” use-cases. */
    suspend fun getFirstName(): String? = withContext(Dispatchers.IO) {
        profileRepository.getFirstName()
    }

    suspend fun getEmail(): String? = withContext(Dispatchers.IO) {
        profileRepository.getEmail()
    }
}
