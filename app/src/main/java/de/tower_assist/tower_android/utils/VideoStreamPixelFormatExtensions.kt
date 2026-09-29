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

package de.tower_assist.tower_android.utils

import com.azure.android.communication.calling.VideoStreamPixelFormat

//
//  VideoStreamPixelFormatExtensions.kt
//  Tower_Android
//

/**
 * The number of bytes per pixel for each buffer of this pixel format.
 */
val VideoStreamPixelFormat.planeSizes: List<Float> get() = when (this) {
    VideoStreamPixelFormat.NV12 -> listOf(1.0f, 0.5f)
    VideoStreamPixelFormat.I420 -> listOf(1.0f, 0.25f, 0.25f)
    VideoStreamPixelFormat.BGR24 -> listOf(3.0f)
    else -> listOf(4.0f)
}
