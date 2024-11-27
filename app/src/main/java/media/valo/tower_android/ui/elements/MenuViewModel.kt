/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.elements

//
//  MenuViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
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
    val usernameFlow: Flow<String?> = preferencesManager.credentialRepository.usernameFlow

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
