/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.credentials

//
//  CredentialRepository.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import media.valo.tower_android.model.Credential
import javax.inject.Inject

/**
 * Repository for credentials.
 *
 * @param credentialDataSource  `CredentialDataSource` dependency.
 */
class CredentialRepository @Inject constructor(
    private val credentialDataSource: CredentialDataSource
) {

    /**
     * A `Flow` that emits the username every time it is updated.
     *
     * This will emit the new username each time the username is set. When the username is unset, it
     * will emit `null`.
     */
    val usernameFlow: Flow<String?> = credentialDataSource.usernameFlow

    /**
     * Get the username (if any).
     *
     * @return The username that is currently set, or `null` if the username is unset.
     */
    suspend fun getUsername(): String? = usernameFlow.firstOrNull()

    /**
     * Change the username.
     *
     * @param username  The new username to set, or `null` to unset the username.
     */
    suspend fun setUsername(username: String?) = credentialDataSource.setUsername(username)

    /**
     * A `Flow` that emits the password every time it is updated.
     *
     * This will emit the new password each time the password is set. When the password is unset, it
     * will emit `null`.
     */
    val passwordFlow: Flow<String?> = credentialDataSource.passwordFlow

    /**
     * Get the password (if any).
     *
     * @return The password that is currently set, or `null` if the password is unset.
     */
    suspend fun getPassword(): String? = passwordFlow.firstOrNull()

    /**
     * Change the password.
     *
     * @param password  The new password to set, or `null` to unset the password.
     */
    suspend fun setPassword(password: String?) = credentialDataSource.setPassword(password)

    /**
     * A `Flow` that emits the `Credential` every time it is updated.
     *
     * This will emit each time either the username or the password are updated. If both are set, it
     * will emit a `Credential`. If either is unset, it emits `null`.
     */
    val credentialFlow: Flow<Credential?> = credentialDataSource.credentialFlow

    /**
     * Get the `Credential` (if any).
     *
     * @return The `Credential` if both username and password are currently set, `null` otherwise.
     */
    suspend fun getCredential(): Credential? = credentialFlow.firstOrNull()

    /**
     * Change the username and password.
     *
     * @param credential    The username and password to set, or `null` to unset both.
     */
    suspend fun setCredential(credential: Credential?) =
        credentialDataSource.setCredential(credential)

    /**
     * Check whether the repository currently holds a credential.
     *
     * @return Whether both username and password are set to a non-blank value.
     */
    suspend fun hasCredential(): Boolean =
        !getUsername().isNullOrBlank() && !getPassword().isNullOrBlank()

}
