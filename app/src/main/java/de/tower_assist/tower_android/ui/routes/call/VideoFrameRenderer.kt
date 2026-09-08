/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.call

import android.app.Activity
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.core.graphics.createBitmap
import com.azure.android.communication.calling.RawVideoFrameBuffer
import com.azure.android.communication.calling.ScalingMode
import com.azure.android.communication.calling.VideoStreamPixelFormat
import io.ktor.util.moveToByteArray
import de.tower_assist.tower_android.utils.isOdd
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min

//
//  VideoFrameRenderer.kt
//  Tower_Android
//

private const val BGRX_PIXEL_STRIDE = 4
private const val BGR24_PIXEL_STRIDE = 3
private const val RGBX_PIXEL_STRIDE = 4
private const val RGBA_PIXEL_STRIDE = 4
private const val ROTATION_STEP = 90

/**
 * Renders a preview of the video into a `SurfaceView`.
 *
 * @param activity      The `Activity` the view should be rendered into.
 * @param scalingMode   Whether to fill the View by cropping the video, or scale the video to fit.
 */
class VideoFrameRenderer(private val activity: Activity, private val scalingMode: ScalingMode) {

    /**
     * The view the video will be rendered into.
     */
    val view: SurfaceView = {
        val surfaceView = SurfaceView(activity)
        surfaceView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        surfaceView
    }()

    /**
     * Render the given frame into the `view`.
     *
     * @param rawVideoFrameBuffer   The video frame to render.
     * @param rotationAngle         The number of degrees of clockwise rotation to apply before rendering.
     * @param mirror                Whether to mirror the video frame before rendering.
     */
    fun render(rawVideoFrameBuffer: RawVideoFrameBuffer, rotationAngle: Int = 0, mirror: Boolean = false) {
        val format = rawVideoFrameBuffer.streamFormat

        val bytes = when (format.pixelFormat) {
            VideoStreamPixelFormat.BGRX ->
                convertBGRToRGBA(rawVideoFrameBuffer.buffers[0].moveToByteArray(), BGRX_PIXEL_STRIDE)
            VideoStreamPixelFormat.BGR24 ->
                convertBGRToRGBA(rawVideoFrameBuffer.buffers[0].moveToByteArray(), BGR24_PIXEL_STRIDE)
            VideoStreamPixelFormat.RGBX ->
                convertRGBXToRGBA(rawVideoFrameBuffer.buffers[0].moveToByteArray())
            VideoStreamPixelFormat.RGBA ->
                rawVideoFrameBuffer.buffers[0].moveToByteArray()
            VideoStreamPixelFormat.NV12 ->
                convertYUVToRGBA(rawVideoFrameBuffer.buffers.map { it.moveToByteArray() }, format.width)
            VideoStreamPixelFormat.I420 ->
                convertYUVToRGBA(rawVideoFrameBuffer.buffers.map { it.moveToByteArray() }, format.width)
        }
        val buffer = ByteBuffer.allocateDirect(bytes.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(bytes)
        buffer.rewind()

        val subsamplingRatio = when (format.pixelFormat) {
            VideoStreamPixelFormat.NV12, VideoStreamPixelFormat.I420 -> 2
            else -> 1
        }
        val invertSize = (rotationAngle / ROTATION_STEP).isOdd
        val w = format.width / subsamplingRatio
        val h = format.height / subsamplingRatio
        val horizontalSizeRatio = view.width.toFloat() / (if (invertSize) h else w).toFloat()
        val verticalSizeRatio = view.height.toFloat() / (if (invertSize) w else h).toFloat()
        val scale = when (scalingMode) {
            ScalingMode.CROP -> max(horizontalSizeRatio, verticalSizeRatio)
            ScalingMode.FIT -> min(horizontalSizeRatio, verticalSizeRatio)
        }

        val matrix = Matrix()
        matrix.setRotate(rotationAngle.toFloat())
        matrix.postScale(if (mirror) -scale else scale, scale)

        val originalBitmap = createBitmap(w, h)
        originalBitmap.copyPixelsFromBuffer(buffer)
        buffer.rewind()

        val resizedBitmap = createBitmap(originalBitmap, 0, 0, w, h, matrix, false)
        originalBitmap.recycle()

        if (view.holder.surface.isValid) {
            val canvas = view.holder.lockCanvas()
            val leftOffset = (canvas.width - (if (invertSize) h else w) * scale) / 2f
            val topOffset = (canvas.height - (if (invertSize) w else h) * scale) / 2f
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            canvas.drawBitmap(resizedBitmap, leftOffset, topOffset, null)
            resizedBitmap.recycle()
            view.holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun convertBGRToRGBA(input: ByteArray, pixelStride: Int): ByteArray {
        val output = ByteArray(input.size / pixelStride * RGBA_PIXEL_STRIDE)
        for (i in 0..<input.size step pixelStride) {
            output[i] = input[i + 2]
            output[i + 1] = input[i + 1]
            output[i + 2] = input[i]
            output[i + 3] = Byte.MAX_VALUE
        }
        return output
    }

    private fun convertRGBXToRGBA(input: ByteArray): ByteArray {
        val output = ByteArray(input.size)
        for (i in 0..<input.size step RGBX_PIXEL_STRIDE) {
            output[i] = if (i % RGBX_PIXEL_STRIDE == RGBX_PIXEL_STRIDE - 1) Byte.MAX_VALUE else input[i]
        }
        return output
    }

    private fun convertYUVToRGBA(input: List<ByteArray>, rowStride: Int): ByteArray {
        val interleaved = input.count() == 2
        val outputResolution = input[0].size / 2 / 2
        val output = ByteArray(outputResolution * RGBA_PIXEL_STRIDE)

        for (i in 0..<outputResolution) {
            val row = i * 2 / rowStride
            val col = i - row * rowStride / 2

            val y = input[0][row * rowStride * 2 + col * 2].toUByte().toInt() - 16
            val u = input[1][i * if (interleaved) 2 else 1].toUByte().toInt() - 128
            val v = (if (interleaved) input[1][i * 2 + 1] else input[2][i]).toUByte().toInt() - 128

            val r = (1.164 * y + 1.596 * v).coerceIn(0.0, 255.0).toInt().toByte()
            val g = (1.164 * y - 0.183 * v + 0.392 * u).coerceIn(0.0, 255.0).toInt().toByte()
            val b = (1.164 * y + 2.018 * u).coerceIn(0.0, 255.0).toInt().toByte()
            val a = 255.toByte()

            output[i * RGBA_PIXEL_STRIDE] = r
            output[i * RGBA_PIXEL_STRIDE + 1] = g
            output[i * RGBA_PIXEL_STRIDE + 2] = b
            output[i * RGBA_PIXEL_STRIDE + 3] = a
        }

        return output
    }

}
