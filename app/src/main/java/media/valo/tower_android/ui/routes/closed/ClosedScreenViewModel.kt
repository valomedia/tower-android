/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.screens.closed

//
//  ClosedScreenViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import javax.inject.Inject

/**
 * `ViewModel` for `ClosedScreen`.
 *
 * Provides read-only accessors to selected profile fields so the screen can prefill
 * contact forms or deep links without owning the `ProfileRepository`.
 *
 * @param profileRepository `ProfileRepository` dependency.
 */
@HiltViewModel
class ClosedScreenViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    /** Suspend functions for “grab it right now” use-cases. */

    /**
     * Get the first name (if any).
     */
    suspend fun getFirstName(): String? = withContext(Dispatchers.IO) {
        profileRepository.getFirstName()
    }

    /**
     * Get the e-mail address (if any).
     */
    suspend fun getEmail(): String? = withContext(Dispatchers.IO) {
        profileRepository.getEmail()
    }
}
