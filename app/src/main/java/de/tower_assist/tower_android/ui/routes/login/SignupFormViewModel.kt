/******************************************************************************
 * Copyright (c) 2025-2026 valo.media GmbH                                    *
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

package de.tower_assist.tower_android.ui.routes.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import de.tower_assist.tower_android.data.local.preferences.profile.ProfileRepository
import de.tower_assist.tower_android.data.local.preferences.settings.SettingsRepository
import de.tower_assist.tower_android.data.remote.newsletter.NewsletterRepository
import de.tower_assist.tower_android.ui.routes.news.newestNewsVersion
import de.tower_assist.tower_android.utils.AppScope
import javax.inject.Inject

//
//  SignupFormViewModel.kt
//  Tower_Android
//

/**
 * ViewModel for SignupForm.
 *
 * @param profileRepository     ProfileRepository dependency.
 * @param newsletterRepository  NewsletterRepository dependency.
 * @param settingsRepository    SettingsRepository dependency.
 * @param appScope              Scope for work that has to outlive this screen.
 */
@HiltViewModel
class SignupFormViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val newsletterRepository: NewsletterRepository,
    private val settingsRepository: SettingsRepository,
    @AppScope val appScope: CoroutineScope
) : ViewModel() {

    /**
     * Record the current release notes as seen, without showing them.
     *
     * Anybody shown the signup form is installing the app rather than upgrading it, so the release
     * notes cover releases they were never around for. They still get the news for later releases.
     */
    suspend fun markNewsAsSeen() = settingsRepository.setLastSeenNewsVersion(newestNewsVersion)

    /**
     * Get the first name (if any).
     *
     * @return The first name that is currently set, or `null` if the first name is unset.
     */
    suspend fun getFirstName(): String? = profileRepository.getFirstName()

    /**
     * Set the first name.
     *
     * @param firstName The new name to set, or `null` to unset the name.
     */
    suspend fun setFirstName(firstName: String?) = profileRepository.setFirstName(firstName)

    /**
     * Get the last name (if any).
     *
     * @return The last name that is currently set, or `null` if the last name is unset.
     */
    suspend fun getLastName(): String? = profileRepository.getLastName()

    /**
     * Set the last name.
     *
     * @param lastName The new name to set, or `null` to unset the name.
     */
    suspend fun setLastName(lastName: String?) = profileRepository.setLastName(lastName)

    /**
     * Get the e-mail address (if any).
     *
     * @return The e-mail address that is currently set, or `null` if the e-mail address is unset.
     */
    suspend fun getEmail(): String? = profileRepository.getEmail()

    /**
     * Set the e-mail address.
     *
     * @param email The new e-mail address to set, or `null` to unset the e-mail address.
     */
    suspend fun setEmail(email: String?) = profileRepository.setEmail(email)

    /**
     * Post a request to either the newsletter or the contacts-only endpoint,
     * handing over first name, email and optionally last name.
     *
     * @param wantsNewsletter `true` to subscribe to the newsletter, `false` to only add a contact.
     */
    suspend fun submitSignup(wantsNewsletter: Boolean) = newsletterRepository.subscribe(wantsNewsletter)

}
