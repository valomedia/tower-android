/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.credentials

//
//  CredentialDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import kotlinx.coroutines.flow.Flow
import media.valo.tower_android.model.Credential

/**
 * A data source for credentials.
 */
interface CredentialDataSource {

    /**
     * A Flow that emits the user id every time it is updated.
     *
     * This will emit the new user id each time the user id is set. When the user id is unset, it
     * will emil `null`.
     */
    val userIdFlow: Flow<String?>

    /**
     * Change the user id.
     *
     * @param userId The new user id to set, or `null` to unset the user id.
     */
    suspend fun setUserId(userId: String?)

    /**
     * A `Flow` that emits the username every time it is updated.
     *
     * This will emit the new username each time the username is set. When the username is unset, it
     * will emit `null`.
     */
    val usernameFlow: Flow<String?>

    /**
     * Change the username.
     *
     * @param username  The new username to set, or `null` to unset the username.
     */
    suspend fun setUsername(username: String?)

    /**
     * A `Flow` that emits the password every time it is updated.
     *
     * This will emit the new password each time the password is set. When the password is unset, it
     * will emit `null`.
     */
    val passwordFlow: Flow<String?>

    /**
     * Change the password.
     *
     * @param password  The new password to set, or `null` to unset the password.
     */
    suspend fun setPassword(password: String?)

    /**
     * A `Flow` that emits the `Credential` every time it is updated.
     *
     * This will emit each time either the username or the password are updated. If both are set, it
     * will emit a `Credential`. If either is unset, it emits `null`.
     */
    val credentialFlow: Flow<Credential?>

    /**
     * Change the username and password.
     *
     * @param credential    The username and password to set, or `null` to unset both.
     */
    suspend fun setCredential(credential: Credential?)
}
