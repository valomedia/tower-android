/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
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

@Serializable
data class InitialContactResponse(
    val message: String,
    val apiVersion: String,
    val openingHours: OpeningHours
)

/**
 * A dummy instance of `InitialContactResponse`.
 */
val dummyInitialContactResponse = InitialContactResponse(message = "Success", apiVersion = "1.0", openingHours = dummyOpeningHours)
