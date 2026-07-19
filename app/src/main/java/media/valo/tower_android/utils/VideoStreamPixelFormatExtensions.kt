/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

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
