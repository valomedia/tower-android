/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

import com.azure.android.communication.calling.VideoStreamFormat
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.ceil

//
//  VideoStreamFormatExtensions.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

fun VideoStreamFormat.allocateBuffers(): List<ByteBuffer> = pixelFormat.planeSizes.map { planeSize ->
    ByteBuffer.allocateDirect(ceil(width * height * planeSize).toInt()).order(ByteOrder.nativeOrder())
}
