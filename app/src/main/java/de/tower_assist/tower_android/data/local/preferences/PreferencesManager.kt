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

package de.tower_assist.tower_android.data.local.preferences

//
//  PreferencesManager.kt
//  Tower_Android
//

import de.tower_assist.tower_android.data.local.preferences.credentials.CredentialRepository
import de.tower_assist.tower_android.data.local.preferences.profile.ProfileRepository
import de.tower_assist.tower_android.data.local.preferences.settings.SettingsRepository
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
