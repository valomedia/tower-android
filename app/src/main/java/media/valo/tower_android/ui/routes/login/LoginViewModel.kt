/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.login

//
//  LoginViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import javax.inject.Inject

/**
 * `ViewModel` for `LoginScreen`.
 *
 * @param credentialRepository  `CredentialRepository` dependency.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val credentialRepository: CredentialRepository
) : ViewModel() {

    /**
     * Get the username (if any).
     *
     * @return The username that is currently set, or `null` if the username is unset.
     */
    suspend fun getUsername(): String? = credentialRepository.getUsername()

    /**
     * Change the username.
     *
     * @param username  The new username to set, or `null` to unset the username.
     */
    suspend fun setUsername(username: String?) = credentialRepository.setUsername(username)

    /**
     * Get the password (if any).
     *
     * @return The password that is currently set, or `null` if the password is unset.
     */
    suspend fun getPassword(): String? = credentialRepository.getPassword()

    /**
     * Change the password.
     *
     * @param password  The new password to set, or `null` to unset the password.
     */
    suspend fun setPassword(password: String?) = credentialRepository.setPassword(password)

    /**
     * Check whether the repository currently holds a credential.
     *
     * @return Whether both username and password are set to a non-blank value.
     */
    suspend fun hasCredential(): Boolean = credentialRepository.hasCredential()

    /**
     * Unset the username and password in the `CredentialRepository`.
     */
    suspend fun clearCredential() = credentialRepository.setCredential(null)

}
