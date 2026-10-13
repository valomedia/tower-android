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

package media.valo.tower_android.utils

import com.azure.android.communication.calling.VideoStreamResolution

//
//  VideoStreamResolutionExtensions.kt
//  Tower_Android
//

/**
 * The width in pixels of the video resolution standard.
 */
val VideoStreamResolution.width: Int get() = when (this) {
    VideoStreamResolution.UNKNOWN -> 0
    VideoStreamResolution.P1080, VideoStreamResolution.FULL_HD -> 1920
    VideoStreamResolution.P720, VideoStreamResolution.HD -> 1280
    VideoStreamResolution.P540 -> 960
    VideoStreamResolution.P480 -> 858
    VideoStreamResolution.P360 -> 640
    VideoStreamResolution.P270 -> 480
    VideoStreamResolution.P240 -> 352
    VideoStreamResolution.P180 -> 320
    VideoStreamResolution.VGA -> 640
    VideoStreamResolution.QVGA -> 320
}

/**
 * The height in pixels of the video resolution standard.
 */
val VideoStreamResolution.height: Int get() = when (this) {
    VideoStreamResolution.UNKNOWN -> 0
    VideoStreamResolution.P1080, VideoStreamResolution.FULL_HD -> 1080
    VideoStreamResolution.P720, VideoStreamResolution.HD -> 720
    VideoStreamResolution.P540 -> 540
    VideoStreamResolution.P480 -> 480
    VideoStreamResolution.P360 -> 360
    VideoStreamResolution.P270 -> 270
    VideoStreamResolution.P240 -> 240
    VideoStreamResolution.P180 -> 180
    VideoStreamResolution.VGA -> 480
    VideoStreamResolution.QVGA -> 240
}
