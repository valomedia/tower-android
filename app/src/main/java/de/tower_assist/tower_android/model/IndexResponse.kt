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

//
//  HttpTowerDataSource.kt
//  Tower_Android
//

import kotlinx.serialization.Serializable

/**
 * The data returned by the `/`-endpoint.
 *
 * @param message message from the endpoint, typically 'success', indicating that the backend is reachable
 * @param apiVersion the current backend api version
 * @param openingHours nested openingHours object, containing various information about the service's availability
 */
@Serializable
data class IndexResponse(
    val message: String,
    val apiVersion: String,
    val openingHours: OpeningHours
)

/**
 * A dummy instance of `IndexResponse`.
 */
val dummyIndexResponse = IndexResponse(message = "Success", apiVersion = "1.0", openingHours = dummyOpeningHours)
