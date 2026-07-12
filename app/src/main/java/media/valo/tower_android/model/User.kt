/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

import kotlinx.serialization.Serializable

/**
 * A username associated with an ID for Azure Communication Services.
 *
 * @param username              The username the user uses to sign in.
 * @param  communicationUserId  Id of the user as used by ACS.
 */
@Serializable
data class User(
    val username: String,
    val communicationUserId: String
)

/**
 * A dummy instance of `User`.
 */
val dummyUser = User(username = "", communicationUserId = "")
