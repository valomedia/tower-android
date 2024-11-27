/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

//
//  FakeCredentialsDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import media.valo.tower_android.data.local.preferences.credentials.CredentialDataSource
import media.valo.tower_android.model.Credential

private const val USERNAME = "theo.test"
private const val PASSWORD = "Tr0ub4dor&3"

/**
 * A fake implementation of `CredentialDataSource`.
 *
 * This discards anything put into it and will emit static (but reasonable) values for all fields.
 */
class FakeCredentialDataSource : CredentialDataSource {

    override val usernameFlow: Flow<String?> = flow { emit(USERNAME) }

    override suspend fun setUsername(username: String?) = Unit

    override val passwordFlow: Flow<String?> = flow { emit(PASSWORD) }

    override suspend fun setPassword(password: String?) = Unit

    override val credentialFlow: Flow<Credential?> =
        flow { emit(Credential(username = USERNAME, password = PASSWORD)) }

    override suspend fun setCredential(credential: Credential?) = Unit

}
