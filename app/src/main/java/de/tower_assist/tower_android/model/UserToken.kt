/******************************************************************************
 * Copyright (c) 2024-2026.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
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
