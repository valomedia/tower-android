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

package media.valo.tower_android.model

import android.location.Location
import kotlinx.serialization.Serializable

//
//  Coordinate.kt
//  Tower_Android
//

/**
 * A latitude and longitude.
 *
 * @param latitude  The latitude in degrees.
 * @param longitude The longitude in degrees.
 */
@Serializable
data class Coordinate(
    val latitude: Double,
    val longitude: Double
) {

    /**
     * Get the Coordinate for an existing Location.
     *
     * @param location The Location to construct a Coordinate for.
     */
    constructor(location: Location): this(location.latitude, location.longitude)

}
