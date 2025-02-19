/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import media.valo.tower_android.BuildConfig

//
//  ClientInfo.java
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * Information about the app.
 */
@Serializable
object ClientInfo {

    /**
     * The package identifier of the application.
     */
    @SerialName("identifier")
    const val IDENTIFIER = BuildConfig.APPLICATION_ID

    /**
     * The version name of the application.
     */
    @SerialName("version")
    const val VERSION = BuildConfig.VERSION_NAME

}
