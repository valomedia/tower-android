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

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import de.tower_assist.tower_android.BuildConfig

//
//  ClientInfo.java
//  Tower_Android
//

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
