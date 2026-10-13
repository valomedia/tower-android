/******************************************************************************
 * Copyright (c) 2026 valo.media GmbH                                         *
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
//  AwaitAssistanceResponse.kt
//  Tower_Android
//
//  Created by:
//      * Yatsar (Agent)
//

/**
 * The data returned by the `/awaitAssistance`-endpoint.
 *
 * @param position The zero-indexed position of the user in the queue.
 */
@Serializable
data class AwaitAssistanceResponse(
    val position: Int
)

/**
 * A dummy instance of `AwaitAssistanceResponse`.
 */
val dummyAwaitAssistanceResponse = AwaitAssistanceResponse(position = 0)
