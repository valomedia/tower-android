/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.news

//
//  LoadingViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Jan Hofherr
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import javax.inject.Inject

/**
 * `ViewModel` for `NewsScreen`.
 *
 * @param settingsRepository    `SettingsRepository` dependency.
 */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    /**
     * Change the version on the last login.
     *
     * @param version Current version.
     */
    suspend fun setLastLoginAppVersion(version: String?) =
        settingsRepository.setLastLoginAppVersion(version)
}
