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

/**
 * Allocate the buffers needed to produce a `RawVideoFrameBuffer` with this format.
 *
 * This will allocate buffers as needed for the `width`, `height`, and `pixelFormat` of this format. The returned list
 * may contain anywhere from one to three buffers, depending on the `pixelFormat`.
 *
 * @return A list of directly allocated `ByteBuffers` with the right sizes for frames of this format.
 */
fun VideoStreamFormat.allocateBuffers(): List<ByteBuffer> = pixelFormat.planeSizes.map { planeSize ->
    ByteBuffer.allocateDirect(ceil(width * height * planeSize).toInt()).order(ByteOrder.nativeOrder())
}
