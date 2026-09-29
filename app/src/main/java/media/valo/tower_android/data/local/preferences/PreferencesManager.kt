/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences

//
//  PreferencesManager.kt
//  Tower_Android
//

import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import javax.inject.Inject

/**
 * A manager for the various preferences.
 *
 * This is an object that conveniently holds a `SettingsRepository`, `CredentialRepository` and
 * `ProfileRepository`.
 *
 * @param settingsRepository    `SettingsRepository` dependency.
 * @param credentialRepository  `CredentialRepository` dependency.
 * @param profileRepository     `ProfileRepository` dependency.
 */
class PreferencesManager @Inject constructor(
    val settingsRepository: SettingsRepository,
    val credentialRepository: CredentialRepository,
    val profileRepository: ProfileRepository
)
