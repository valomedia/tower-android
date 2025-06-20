/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

//
//  VideoFrameSender.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

import android.graphics.Bitmap
import android.media.Image
import androidx.core.graphics.createBitmap
import com.azure.android.communication.calling.RawVideoFrameBuffer
import com.azure.android.communication.calling.VideoStreamState
import com.azure.android.communication.calling.VirtualOutgoingVideoStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ArrayBlockingQueue

class VideoFrameSender(
    private val rawOutgoingVideoStream: VirtualOutgoingVideoStream?
) {

    private fun sendVideoFrame(videoFrameBuffer: RawVideoFrameBuffer) {
        try {
            rawOutgoingVideoStream?.sendRawVideoFrame(videoFrameBuffer)?.get()
        } catch (e: Exception) {
            throw e
        }
    }

    fun passFrame(image: Image) {
        val videoFrameBuffer = yuvToByteBuffer(image)
        sendVideoFrame(videoFrameBuffer)
    }

    private fun yuvToByteBuffer(image: Image): RawVideoFrameBuffer {
        val width = image.width
        val height = image.height
        val planes = image.planes
        val ySize = width * height
        val uvSize = ySize / 4
        val output = ByteArray(ySize + uvSize * 2)

        val yBuffer = planes[0].buffer
        val yRowStride = planes[0].rowStride
        val yPixelStride = planes[0].pixelStride
        var offset = 0
        val row = ByteArray(yRowStride)
        for (i in 0 until height) {
            yBuffer.position(i * yRowStride)
            yBuffer.get(row, 0, yRowStride)
            var j = 0
            while (j < width) {
                output[offset++] = row[j * yPixelStride]
                j++
            }
        }

        for (planeIndex in 1..2) {
            val buffer = planes[planeIndex].buffer
            val rowStride = planes[planeIndex].rowStride
            val pixelStride = planes[planeIndex].pixelStride
            val planeWidth = width / 2
            val planeHeight = height / 2
            val planeRow = ByteArray(rowStride)
            for (i in 0 until planeHeight) {
                buffer.position(i * rowStride)
                buffer.get(planeRow, 0, rowStride)
                var j = 0
                while (j < planeWidth) {
                    output[offset++] = planeRow[j * pixelStride]
                    j++
                }
            }
        }

        val buffer = ByteBuffer.wrap(output)

        val videoFrameBuffer = RawVideoFrameBuffer()
        videoFrameBuffer.setBuffers(listOf(buffer))
        videoFrameBuffer.setStreamFormat(rawOutgoingVideoStream?.format)
        return videoFrameBuffer
    }
}
