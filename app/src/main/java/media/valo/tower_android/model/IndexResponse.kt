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
