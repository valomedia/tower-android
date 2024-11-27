/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.settings

//
//  SettingsViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import javax.inject.Inject

/**
 * `ViewModel` for `SettingsScreen`.
 *
 * @param settingsRepository    `SettingsRepository` dependency.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    /**
     * Get the api endpoint for the TOWER api (if any).
     *
     * @return The api endpoint for connections to the TOWER backend, or `null` if it is unset.
     */
    suspend fun getApiEndpoint(): String? = settingsRepository.getApiEndpoint()

    /**
     * Change the api endpoint.
     *
     * This is used to update the api endpoint to use for connections to the TOWER backend (such as
     * when switching between the development and production environments).
     *
     * @param apiEndpoint   The new api endpoint to set, or `null` to unset the api endpoint.
     */
    suspend fun setApiEndpoint(apiEndpoint: String?) =
        settingsRepository.setApiEndpoint(apiEndpoint)

}
