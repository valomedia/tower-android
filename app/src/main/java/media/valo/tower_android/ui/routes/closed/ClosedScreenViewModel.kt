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

package media.valo.tower_android.ui.screens.closed

//
//  AppBarViewModel.kt
//  Tower_Android
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
