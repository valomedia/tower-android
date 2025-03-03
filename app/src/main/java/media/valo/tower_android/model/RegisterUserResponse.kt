/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

import kotlinx.serialization.Serializable

//
//  RegisterUserResponse.java
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * The data returned by the `/registerUser`-endpoint.
 *
 * @param userId The unique identifier this instance of the app will supply when accessing the service.
 */
@Serializable
data class RegisterUserResponse(
    val userId: String
)

/**
 * A dummy instance of `RegisterUserResponse`.
 */
val dummyRegisterUserResponse = RegisterUserResponse(userId = "00000000-0000-0000-0000-000000000000")
