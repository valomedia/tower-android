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

package media.valo.tower_android.model

import com.azure.android.communication.calling.VideoStreamFormat
import com.azure.android.communication.calling.VideoStreamPixelFormat
import com.azure.android.communication.calling.VideoStreamResolution
import media.valo.tower_android.utils.width

//
//  CallQualityLevel.kt
//  Tower_Android
//

/**
 * The various video format the stream will switch to depending on the quality of the connection.
 */
enum class CallQualityLevel {

    /**
     * VGA
     */
    VERY_LOW {
        override val frameRate = 7.5f
        override val resolution = VideoStreamResolution.VGA
    },

    /**
     * 540p15
     */
    LOW {
        override val frameRate = 15f
        override val resolution = VideoStreamResolution.P540
    },

    /**
     * 540p
     */
    MEDIUM {
        override val frameRate = 30f
        override val resolution = VideoStreamResolution.P540
    },

    /**
     * 720p
     */
    HIGH {
        override val frameRate = 30f
        override val resolution = VideoStreamResolution.P720
    },

    /**
     * 1080p
     */
    VERY_HIGH {
        override val frameRate = 30f
        override val resolution = VideoStreamResolution.P1080
    };

    /**
     * The frame rate to use for the video transmission.
     */
    abstract val frameRate: Float

    /**
     * The Azure Communication Services `VideoStreamResolution` that corresponds to the `CallQualityLevel`.
     */
    abstract val resolution: VideoStreamResolution

    /**
     * The Azure Communication Services `VideoStreamFormat` used at each `CallQualityLevel`.
     */
    val videoStreamFormat: VideoStreamFormat get() {
        val format = VideoStreamFormat()
        format.resolution = resolution
        format.pixelFormat = VideoStreamPixelFormat.NV12
        format.framesPerSecond = frameRate
        format.stride1 = resolution.width
        format.stride2 = resolution.width
        return format
    }

}
