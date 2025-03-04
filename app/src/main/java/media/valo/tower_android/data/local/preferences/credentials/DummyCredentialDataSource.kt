/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.credentials

//
//  DummyCredentialDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A dummy implementation of `CredentialDataSource`.
 *
 * This discards anything put into it and will always emit `null` for all fields.
 */
class DummyCredentialDataSource : CredentialDataSource {

    override val userIdFlow: Flow<String?> = flow { emit(null) }

    override suspend fun setUserId(userId: String?) = Unit

}
