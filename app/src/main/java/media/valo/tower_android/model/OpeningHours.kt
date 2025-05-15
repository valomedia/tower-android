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

import kotlinx.serialization.Serializable

//TODO: make time & schedule typesafe

/**
 * The 'OpeningHours' object, nested inside data returned by the `/`-endpoint.
 *
 * @param time current time
 * @param status indicating wether the service is currently closed or not
 * @param schedule schedule of service for the next few days
 * @param description string containig easily readable information about opening hours
 */
@Serializable
data class OpeningHours(
    val time: String,
    val status: String,
    val schedule: Map<String, String>,
    val description: String
)

/**
 * A dummy instance of `OpeningHours`.
 */
val dummyOpeningHours = OpeningHours(
    time = "13:37",
    status = "closed",
    schedule = mapOf(
        "2025-02-14" to "08:00-12:00, 13:00-17:00",
        "2025-02-15" to "",
        "2025-02-16" to "",
        "2025-02-17" to "",
        "2025-02-18" to "12:00-16:00",
        "2025-02-19" to "12:00-16:00",
        "2025-02-20" to "12:00-16:00",
        "2025-02-21" to ""
    ),
    description = "Montag bis Freitag von 8 bis 12 und von 13 bis 17 Uhr."
)
