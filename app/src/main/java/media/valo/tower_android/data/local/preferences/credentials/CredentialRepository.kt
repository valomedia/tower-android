/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.credentials

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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
     * A Flow that emits the user id every time it is updated.
     *
     * This will emit the new user id each time the user id is set. When the user id is unset, it
     * will emil `null`.
     */
    val userIdFlow: Flow<String?> = credentialDataSource.userIdFlow

    /**
     * Get the user id (if any).
     *
     * @return The user id that is currently set, or `null` if the user id is unset.
     */
    suspend fun getUserId(): String? = userIdFlow.firstOrNull()

    /**
     * Change the user id.
     *
     * @param userId The new user id to set, or `null` to unset the user id.
     */
    suspend fun setUserId(userId: String?) = credentialDataSource.setUserId(userId)

}
