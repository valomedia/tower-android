/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.utils

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
