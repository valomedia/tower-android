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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import javax.inject.Inject

/**
 * `ViewModel` for `ClosedScreen`.
 */
@HiltViewModel
class ClosedScreenViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    /** Suspend functions for “grab it right now” use-cases. */
    suspend fun getFirstName(): String? = withContext(Dispatchers.IO) {
        profileRepository.getFirstName()
    }

    suspend fun getEmail(): String? = withContext(Dispatchers.IO) {
        profileRepository.getEmail()
    }
}
