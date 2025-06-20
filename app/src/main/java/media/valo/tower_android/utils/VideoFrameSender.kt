package media.valo.tower_android.utils

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
    @Volatile
    private var stopFrameIterator = false
    private var frameIteratorThread: Thread? = null
    private val imageQueue = ArrayBlockingQueue<Image>(30)

    fun videoFrameIterator() {
        while (!stopFrameIterator) {
            try {

                val stream = rawOutgoingVideoStream
                if (stream != null &&
                    stream.state == VideoStreamState.STARTED &&
                    stream.format != null
                ) {
                    sendVideoFrame()
                } else {
                    Thread.sleep(10)
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun enqueueImage(image: Image) {
        imageQueue.put(image)
    }

    private fun obtainNextVideoFrame(): RawVideoFrameBuffer? {
        val image = imageQueue.take()
        try {
            val yPlane = image.planes[0].buffer.let { buf ->
                buf.rewind()
                val arr = ByteArray(buf.remaining())
                buf.get(arr)
                arr
            }
            val uPlane = image.planes[1].buffer.let { buf ->
                buf.rewind()
                val arr = ByteArray(buf.remaining())
                buf.get(arr)
                arr
            }
            val vPlane = image.planes[2].buffer.let { buf ->
                buf.rewind()
                val arr = ByteArray(buf.remaining())
                buf.get(arr)
                arr
            }
            val yuvData = YuvImageData(
                width = image.width,
                height = image.height,
                yPlane = yPlane,
                uPlane = uPlane,
                vPlane = vPlane,
                yRowStride = image.planes[0].rowStride,
                uvRowStride = image.planes[1].rowStride,
                uvPixelStride = image.planes[1].pixelStride,
                timestamp = image.timestamp
            )
            val bitmap = yuv420ToRgbaBitmap(yuvData)
            val buffer = bitmapToByteBuffer(bitmap)
            val videoFrameBuffer = RawVideoFrameBuffer()
            videoFrameBuffer.setBuffers(listOf(buffer))
            videoFrameBuffer.setStreamFormat(rawOutgoingVideoStream?.format)
            return videoFrameBuffer
        } finally {
            image.close()
        }
    }

    private fun sendVideoFrame() {
        val videoFrame = obtainNextVideoFrame()
        try {
            rawOutgoingVideoStream?.sendRawVideoFrame(videoFrame)?.get()
            val delayBetweenFrames =
                (1000 / (rawOutgoingVideoStream?.format?.framesPerSecond?.toDouble() ?: 30.0)).toInt()
            Thread.sleep(delayBetweenFrames.toLong())
        } catch (e: Exception) {
            throw e
        }
    }

    fun start() {
        frameIteratorThread = Thread { videoFrameIterator() }
        frameIteratorThread?.start()
    }

    fun stop() {
        try {
            if (frameIteratorThread != null) {
                stopFrameIterator = true
                frameIteratorThread?.join()
                frameIteratorThread = null
                stopFrameIterator = false
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun bitmapToByteBuffer(
        bitmap: Bitmap
    ): ByteBuffer {
        val rgbaBuffer = ByteBuffer.allocateDirect(bitmap.byteCount).order(ByteOrder.nativeOrder())
        bitmap.copyPixelsToBuffer(rgbaBuffer)
        rgbaBuffer.rewind()
        return rgbaBuffer
    }

    fun yuv420ToRgbaBitmap(
        yuvImageData: YuvImageData
    ): Bitmap {
        val width = yuvImageData.width
        val height = yuvImageData.height
        val yPlane = yuvImageData.yPlane
        val uPlane = yuvImageData.uPlane
        val vPlane = yuvImageData.vPlane
        val yRowStride = yuvImageData.yRowStride
        val uvRowStride = yuvImageData.uvRowStride
        val uvPixelStride = yuvImageData.uvPixelStride

        val argb = IntArray(width * height)
        for (j in 0 until height) {
            for (i in 0 until width) {
                val yIndex = j * yRowStride + i
                val uvIndex = (j shr 1) * uvRowStride + (i shr 1) * uvPixelStride

                val y = 0xff and yPlane[yIndex].toInt()
                val u = 0xff and uPlane[uvIndex].toInt()
                val v = 0xff and vPlane[uvIndex].toInt()

                val yf = y - 16
                val uf = u - 128
                val vf = v - 128

                val r = (1.164f * yf + 1.596f * vf).toInt().coerceIn(0, 255)
                val g = (1.164f * yf - 0.813f * vf - 0.391f * uf).toInt().coerceIn(0, 255)
                val b = (1.164f * yf + 2.018f * uf).toInt().coerceIn(0, 255)

                argb[j * width + i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            }
        }
        val bitmap = createBitmap(width, height)
        bitmap.setPixels(argb, 0, width, 0, 0, width, height)
        return bitmap
    }
}

data class YuvImageData(
    val width: Int,
    val height: Int,
    val yPlane: ByteArray,
    val uPlane: ByteArray,
    val vPlane: ByteArray,
    val yRowStride: Int,
    val uvRowStride: Int,
    val uvPixelStride: Int,
    val timestamp: Long
)
