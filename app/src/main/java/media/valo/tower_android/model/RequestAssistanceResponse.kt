/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

import kotlinx.serialization.Serializable

//
//  RequestAssistanceResponse.java
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * The data returned by the `/requestAssistance`-endpoint.
 *
 * @param userToken         The access token for the upcoming assistance session.
 * @param keepaliveInterval How often to send a request to the `/awaitAssistance`-endpoint.
 */
@Serializable
data class RequestAssistanceResponse(
    val userToken: UserToken,
    val keepaliveInterval: Int? = null
)

/**
 * A dummy instance of `RequestAssistanceResponse`.
 */
val dummyRequestAssistanceResponse = RequestAssistanceResponse(userToken = dummyUserToken)
