/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.elements

//
//  AppBarViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.model.Credential
import javax.inject.Inject

/**
 * `ViewModel` for `AppBar`.
 *
 * @param credentialRepository  `CredentialRepository` dependency.
 */
@HiltViewModel
class AppBarViewModel @Inject constructor(
    private val credentialRepository: CredentialRepository
) : ViewModel() {

    /**
     * A `Flow` that emits the `Credential` every time it is updated.
     *
     * This will emit each time either the username or the password are updated. If both are set, it
     * will emit a `Credential`. If either is unset, it emits `null`.
     */
    val credentialFlow: Flow<Credential?> = credentialRepository.credentialFlow

    /**
     * Log out the user.
     *
     * This will unset the username and password in the `CredentialRepository`.
     */
    suspend fun logout() = credentialRepository.setCredential(null)

}
