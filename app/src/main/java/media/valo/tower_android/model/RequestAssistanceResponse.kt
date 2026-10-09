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

package media.valo.tower_android.model

import kotlinx.serialization.Serializable

//
//  RequestAssistanceResponse.java
//  Tower_Android
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
