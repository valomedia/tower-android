/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
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
