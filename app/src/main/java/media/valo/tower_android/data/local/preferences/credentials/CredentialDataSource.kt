/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.credentials

import kotlinx.coroutines.flow.Flow

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

}
