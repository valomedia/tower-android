/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
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
//  Created by:
//      * Jean-Pierre Höhmann
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
