/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
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
