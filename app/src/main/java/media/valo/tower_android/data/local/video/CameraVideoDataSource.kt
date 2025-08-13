/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.video

import android.Manifest
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.RequiresPermission
import com.azure.android.communication.calling.RawVideoFrame
import com.azure.android.communication.calling.RawVideoFrameBuffer
import com.azure.android.communication.calling.VideoStreamFormat
import com.azure.android.communication.calling.VideoStreamPixelFormat
import media.valo.tower_android.utils.allocateBuffers
import media.valo.tower_android.utils.isEven
import javax.inject.Inject

//
//  CameraVideoDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

class CameraVideoDataSource @Inject constructor(
    private val cameraManager: CameraManager
): VideoDataSource {

    override var format: VideoStreamFormat? = null
        set(value) {
            field = value
            updateCaptureSession()
        }

    private var cameraDevice: CameraDevice? = null
    private var imageReader: ImageReader? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var cameraThread: HandlerThread? = null
    private var cameraHandler: Handler? = null
    private var imageReaderThread: HandlerThread? = null
    private var imageReaderHandler: Handler? = null
    private var callback: ((RawVideoFrame) -> Unit)? = null

    @RequiresPermission(Manifest.permission.CAMERA)
    override fun start(callback: (RawVideoFrame) -> Unit) {
        this.callback = callback

        val cameraId = cameraManager
            .cameraIdList
            .firstOrNull { id ->
                (cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING)
                        == CameraCharacteristics.LENS_FACING_FRONT)
            }
            ?: cameraManager.cameraIdList.firstOrNull()

        // No cameras available, so don't produce video
        if (cameraId == null) { return }

        val cameraThread = HandlerThread("CameraThread").apply { start() }
        val cameraHandler = Handler(cameraThread.looper)
        this.cameraThread = cameraThread
        this.cameraHandler = cameraHandler

        val imageReaderThread = HandlerThread("ImageReaderThread").apply { start() }
        val imageReaderHandler = Handler(imageReaderThread.looper)
        this.imageReaderThread = imageReaderThread
        this.imageReaderHandler = imageReaderHandler

        cameraManager.openCamera(
            cameraId,
            object: CameraDevice.StateCallback() {

                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    updateCaptureSession()
                }

                @RequiresPermission(Manifest.permission.CAMERA)
                override fun onDisconnected(camera: CameraDevice) {
                    stop()

                    // The camera we were using disappeared, but of any other camera is still available, we can start
                    // again using that camera. If there are no cameras available anymore, this is a no-op.
                    start(callback)
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    stop()
                }

            },
            cameraHandler
        )

    }

    override fun stop() {
        stopCaptureSession()

        cameraDevice?.close()
        cameraThread?.quitSafely()
        imageReaderThread?.quitSafely()

        cameraDevice = null
        cameraThread = null
        cameraHandler = null
        imageReaderThread = null
        imageReaderHandler = null
    }

    private fun updateCaptureSession() {
        val callback = callback
        val cameraDevice = cameraDevice
        val format = format

        if (callback == null || cameraDevice == null || format == null || format.width <= 0 || format.height <= 0) {
            stopCaptureSession()
        } else {
            startCaptureSession(callback = callback, cameraDevice = cameraDevice, format = format)
        }
    }

    private fun startCaptureSession(
        callback: (RawVideoFrame) -> Unit,
        cameraDevice: CameraDevice,
        format: VideoStreamFormat
    ) {
        check(format.pixelFormat == VideoStreamPixelFormat.NV12) { "Pixel format must be NV12." }
        check(format.width.isEven && format.height.isEven) { "Width and height must be divisible by 2." }

        val imageReader = ImageReader.newInstance(format.width, format.height, ImageFormat.YUV_420_888, 2)
        imageReader.setOnImageAvailableListener(
            { reader ->
                val image = reader.acquireLatestImage()
                val yPlane = image.planes[0]
                val uPlane = image.planes[1]
                val vPlane = image.planes[2]

                check(yPlane.rowStride == format.width && uPlane.rowStride == format.width) {
                    "Camera not supported."
                }

                val buffers = format.allocateBuffers()
                buffers[0].put(yPlane.buffer)

                val uvResolution = buffers[1].capacity() / 2
                for (i in 0..<uvResolution) {
                    buffers[1].put(uPlane.buffer.get(i * uPlane.pixelStride))
                    buffers[1].put(vPlane.buffer.get(i * vPlane.pixelStride))
                }

                buffers.map { it.rewind() }

                val videoFrame = RawVideoFrameBuffer()
                videoFrame.streamFormat = format
                videoFrame.buffers = buffers
                videoFrame.use(callback)

                image.close()
            },
            imageReaderHandler
        )
        this.imageReader = imageReader

        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
        captureRequestBuilder.addTarget(imageReader.surface)

        @Suppress("DEPRECATION")
        cameraDevice.createCaptureSession(
            listOf(imageReader.surface),
            object: CameraCaptureSession.StateCallback() {

                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    session.setRepeatingRequest(captureRequestBuilder.build(), null, cameraHandler)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    stopCaptureSession()
                }

            },
            cameraHandler
        )

    }

    private fun stopCaptureSession() {
        imageReader?.setOnImageAvailableListener(null, null)
        cameraCaptureSession?.stopRepeating()

        imageReader = null
        cameraCaptureSession = null
    }

}
