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
import com.azure.android.communication.calling.RawVideoFrameBuffer
import com.azure.android.communication.calling.VideoStreamFormat
import com.azure.android.communication.calling.VideoStreamPixelFormat
import io.ktor.util.moveToByteArray
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

    override var orientation: Int = 0
        private set

    private var cameraDevice: CameraDevice? = null
    private var imageReader: ImageReader? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var cameraThread: HandlerThread? = null
    private var cameraHandler: Handler? = null
    private var imageReaderThread: HandlerThread? = null
    private var imageReaderHandler: Handler? = null
    private var callback: ((RawVideoFrameBuffer) -> Unit)? = null

    @RequiresPermission(Manifest.permission.CAMERA)
    override fun start(callback: (RawVideoFrameBuffer) -> Unit) {
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

        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

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
        callback: (RawVideoFrameBuffer) -> Unit,
        cameraDevice: CameraDevice,
        format: VideoStreamFormat
    ) {
        check(format.pixelFormat == VideoStreamPixelFormat.NV12) { "Pixel format must be NV12." }
        check(format.width.isEven && format.height.isEven) { "Width and height must be divisible by 2." }

        val imageReader = ImageReader.newInstance(format.width, format.height, ImageFormat.YUV_420_888, 2)
        imageReader.setOnImageAvailableListener(
            { reader ->
                val image = reader.acquireLatestImage()
                if (image == null) {
                    return@setOnImageAvailableListener
                }

                check(image.planes[1].rowStride == format.width && image.planes[2].rowStride == format.width) {
                    "Camera not supported."
                }

                val buffers = format.allocateBuffers()
                val uPlane = image.planes[1].buffer.moveToByteArray()
                val vPlane = image.planes[2].buffer.moveToByteArray()
                val uvPlane = ByteArray(buffers[1].capacity())
                val uvResolution = buffers[1].capacity() / 2
                val uvPixelStride = image.planes[1].pixelStride
                for (i in 0..<uvResolution) {
                    uvPlane[2 * i] = uPlane[i * uvPixelStride]
                    uvPlane[2 * i + 1] = vPlane[i * uvPixelStride]
                }
                buffers[0].put(image.planes[0].buffer)
                buffers[1].put(uvPlane)
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
