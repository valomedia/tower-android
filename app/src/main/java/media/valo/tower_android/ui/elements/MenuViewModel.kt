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

package media.valo.tower_android.ui.elements

//
//  MenuViewModel.kt
//  Tower_Android
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import media.valo.tower_android.data.local.preferences.PreferencesManager
import javax.inject.Inject

/**
 * `ViewModel` for `Menu`.
 *
 * @param preferencesManager    `PreferencesManager` dependency.
 */
@HiltViewModel
class MenuViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    /**
     * A `Flow` that emits the username every time it is updated.
     *
     * This will emit the new username each time the username is set. When the username is unset, it
     * will emit `null`.
     */
    val userIdFlow: Flow<String?> = preferencesManager.credentialRepository.userIdFlow

    /**
     * A `Flow` that emits the first name every time it is updated.
     *
     * This will emit the new name each time it is set. When the name is unset, it will emit `null`.
     */
    val firstNameFlow: Flow<String?> = preferencesManager.profileRepository.firstNameFlow

    /**
     * A `Flow` that emits the last name every time it is updated.
     *
     * This will emit the new name each time it is set. When the name is unset, it will emit `null`.
     */
    val lastNameFlow: Flow<String?> = preferencesManager.profileRepository.lastNameFlow

}
