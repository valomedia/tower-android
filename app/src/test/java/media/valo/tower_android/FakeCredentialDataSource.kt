/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

//
//  FakeCredentialsDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import media.valo.tower_android.data.local.preferences.credentials.CredentialDataSource

private const val USER_ID = "00000000-0000-0000-0000-000000000000"

/**
 * A fake implementation of `CredentialDataSource`.
 *
 * This discards anything put into it and will emit static (but reasonable) values for all fields.
 */
class FakeCredentialDataSource : CredentialDataSource {

    override val userIdFlow: Flow<String?> = flow { emit(USER_ID) }

    override suspend fun setUserId(userId: String?) = Unit

}
