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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The 'OpeningHours' object, nested inside data returned by the `/`-endpoint.
 *
 * @param status indicating wether the service is currently closed or not
 * @param description string containig easily readable information about opening hours
 */
@Serializable
data class OpeningHours(
    val status: Status,
    val description: String
)

/**
 * The 'Status' object, indicating wether the Service is 'open' or 'closed'.
 */
@Serializable
enum class Status{
    @SerialName("open")
    OPEN,

    @SerialName("closed")
    CLOSED
}

/**
 * A dummy instance of `OpeningHours`.
 */
val dummyOpeningHours = OpeningHours(
    status = Status.OPEN,
    description = "Montag bis Freitag von 8 bis 12 und von 13 bis 17 Uhr."
)
