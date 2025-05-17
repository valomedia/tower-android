/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

//
//  HttpTowerDataSource.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
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
