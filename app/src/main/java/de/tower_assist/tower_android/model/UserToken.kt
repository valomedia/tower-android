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

package de.tower_assist.tower_android.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock

//
//  UserToken.java
//  Tower_Android
//

/**
 * A user associated with an access token.
 *
 * @param user      The `User` this `UserToken` is for.
 * @param token     The access token issued for the user.
 * @param expiresOn The expiry time of the token.
 */
@OptIn(kotlin.time.ExperimentalTime::class)
@Serializable
data class UserToken(
    val user: User,
    val token: String,
    val expiresOn: kotlin.time.Instant
)

/**
 * A dummy instance of `UserToken`.
 */
@OptIn(kotlin.time.ExperimentalTime::class)
val dummyUserToken = UserToken(
    user = dummyUser,
    token = "",
    expiresOn = Clock.System.now()
)
