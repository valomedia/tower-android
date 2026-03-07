/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.Serializable

//
//  UserToken.java
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
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
    @Serializable(InstantIso8601Serializer::class)
    val expiresOn: Instant
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
