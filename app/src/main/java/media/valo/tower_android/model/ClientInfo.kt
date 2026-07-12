/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import media.valo.tower_android.BuildConfig

/**
 * Information about the app.
 *
 * @param identifier    The package identifier of the application.
 * @param version       The version name of the application.
 */
@Serializable
data class ClientInfo(
    @Required val identifier: String = BuildConfig.APPLICATION_ID,
    @Required val version: String = BuildConfig.VERSION_NAME
)
