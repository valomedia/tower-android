/******************************************************************************
 * Copyright (c) 2024-2026 valo.media GmbH                                    *
 * All rights reserved.                                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Affero General Public License as             *
 * published by the Free Software Foundation, either version 3 of the         *
 * License, or (at your option) any later version.                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Affero General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Affero General Public License   *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package de.tower_assist.tower_android.data.local.preferences.credentials

//
//  CredentialRepository.kt
//  Tower_Android
//

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
