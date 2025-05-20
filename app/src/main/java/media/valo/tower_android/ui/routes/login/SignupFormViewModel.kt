/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.remote.newsletter.NewsletterRepository
import media.valo.tower_android.utils.AppScope
import java.util.regex.Pattern
import javax.inject.Inject

//
//  SignupFormViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * ViewModel for SignupForm.
 *
 * @param profileRepository ProfileRepository dependency.
 */
@HiltViewModel
class SignupFormViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val newsletterRepository: NewsletterRepository,
    @AppScope private val appScope: CoroutineScope
) : ViewModel() {

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

    fun subscribeToNewsletter() = appScope.launch{ newsletterRepository.subscribe() }

    /**
     * This is a helper function, using regex to check if the currently entered Email is a valid one. (format wise)
     *
     * This will then return either true or false, helping show the Newsletter signup checkbox only, if the mail is valid.
     */
    fun isEmailValid(
        email: String
    ): Boolean {
        val pattern = Pattern.compile(
            "^(?=.{4,}$)[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+$"
        )
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}
