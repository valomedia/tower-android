/******************************************************************************
 * Copyright (c) 2025-2026 valo.media GmbH                                    *
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

//
//  RegisterUserResponse.java
//  Tower_Android
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
