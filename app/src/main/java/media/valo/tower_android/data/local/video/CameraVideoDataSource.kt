/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.video

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import androidx.annotation.RequiresPermission
import androidx.core.util.component1
import androidx.core.util.component2
import com.azure.android.communication.calling.RawVideoFrameBuffer
import com.azure.android.communication.calling.VideoStreamFormat
import com.azure.android.communication.calling.VideoStreamPixelFormat
import io.ktor.util.moveToByteArray
import media.valo.tower_android.model.CallQualityLevel
import media.valo.tower_android.utils.allocateBuffers
import media.valo.tower_android.utils.isEven
import javax.inject.Inject

//
//  CameraVideoDataSource.kt
//  Tower_Android
//

private const val FULL_ROTATION = 360

/**
 * A `VideoDataSource` fed by a camera.
 *
 * @param cameraManager `CameraManager` dependency.
 */
class CameraVideoDataSource @Inject constructor(
    private val cameraManager: CameraManager
): VideoDataSource {

    override var format: VideoStreamFormat? = null
        set(value) {
            field = value
            updateCaptureSession()
        }

    override val shouldMirrorPreview: Boolean by this::isCameraFacingUser

    override var photoSize: Size = Size(
        CallQualityLevel.VERY_HIGH.videoStreamFormat.width,
        CallQualityLevel.VERY_HIGH.videoStreamFormat.height
    )
        private set

    private val lensFacing: Int get() = if (isCameraFacingUser) {
        CameraCharacteristics.LENS_FACING_FRONT
    } else {
        CameraCharacteristics.LENS_FACING_BACK
    }

    private var isCameraFacingUser = false
    private var sensorOrientation: Int = 0
    private var cameraDevice: CameraDevice? = null
    private var videoImageReader: ImageReader? = null
    private var photoImageReader: ImageReader? = null
    private var latestImage: Image? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var cameraThread: HandlerThread? = null
    private var cameraHandler: Handler? = null
    private var imageReaderThread: HandlerThread? = null
    private var imageReaderHandler: Handler? = null
    private var callback: ((RawVideoFrameBuffer) -> Unit)? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var torchEnabled: Boolean = false
    private var currentCameraId: String? = null
    private var flashAvailable: Boolean = false

    @RequiresPermission(Manifest.permission.CAMERA)
    override fun start(callback: (RawVideoFrameBuffer) -> Unit) {
        stop()
        this.callback = callback

        val cameraThread = HandlerThread("CameraThread").apply { start() }
        val cameraHandler = Handler(cameraThread.looper)
        this.cameraThread = cameraThread
        this.cameraHandler = cameraHandler

        val imageReaderThread = HandlerThread("ImageReaderThread").apply { start() }
        val imageReaderHandler = Handler(imageReaderThread.looper)
        this.imageReaderThread = imageReaderThread
        this.imageReaderHandler = imageReaderHandler

        openCamera()
    }

    override fun stop() {
        stopCaptureSession()

        cameraHandler?.removeCallbacksAndMessages(null)

        cameraDevice?.close()
        cameraThread?.quitSafely()
        imageReaderThread?.quitSafely()

        isCameraFacingUser = false
        cameraDevice = null
        sensorOrientation = 0
        cameraThread = null
        cameraHandler = null
        imageReaderThread = null
        imageReaderHandler = null
        callback = null
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    override fun switchSource() {
        closeCamera()
        isCameraFacingUser = !isCameraFacingUser
        openCamera()
    }

    override fun takePhoto(): Bitmap? {
        val image = latestImage
        if (image == null) {
            println("No photo to take")
            return null
        }
        val bytes = image.planes[0].buffer.moveToByteArray()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun rotationFor(orientation: Int): Int {
        return (FULL_ROTATION + sensorOrientation + orientation * if (isCameraFacingUser) 1 else -1) % FULL_ROTATION
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private fun openCamera() {
        val cameraId = cameraManager
            .cameraIdList
            .firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING) == lensFacing
            }
            ?: cameraManager.cameraIdList.firstOrNull()

        // No cameras available, so don't produce video
        if (cameraId == null) { return }

        // + keep track of the active id and whether it has a flash
        currentCameraId = cameraId
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
        photoSize = characteristics
            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            .getOutputSizes(ImageFormat.JPEG)!!
            .maxBy { (width, height) -> width * height }

        cameraManager.openCamera(
            cameraId,
            object: CameraDevice.StateCallback() {

                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    updateCaptureSession()
                }

                @RequiresPermission(Manifest.permission.CAMERA)
                override fun onDisconnected(camera: CameraDevice) {
                    // The camera we were using disappeared, but of any other camera is still available, we can start
                    // again using that camera. If there are no cameras available anymore, this is a no-op.
                    closeCamera()
                    openCamera()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    stop()
                }

            },
            cameraHandler
        )

    }

    private fun closeCamera() {
        stopCaptureSession()
        cameraDevice?.close()
    }

    /**
     * Enable or disable the device torch while the capture session is running.
     */
    fun setTorchEnabled(enabled: Boolean) {
        torchEnabled = enabled
        if (!flashAvailable) return

        val builder = captureRequestBuilder
        val session = cameraCaptureSession
        if (builder != null && session != null) {
            val action = Runnable {
                // If the session/builder got replaced (camera switch), do nothing.
                if (session !== cameraCaptureSession || builder !== captureRequestBuilder) return@Runnable

                builder.set(
                    CaptureRequest.FLASH_MODE,
                    if (torchEnabled) CaptureRequest.FLASH_MODE_TORCH else CaptureRequest.FLASH_MODE_OFF
                )
                // Ignore if the camera got closed between here and the call.
                try {
                    session.setRepeatingRequest(builder.build(), null, cameraHandler)
                } catch (_: IllegalStateException) {
                    // no-op: camera/session was closed or in error state
                }
            }

            val handler = cameraHandler
            if (handler != null) {
                handler.post(action)
            } else {
                action.run()
            }
        }
    }

    /** Whether torch is currently requested to be on. */
    fun isTorchEnabled(): Boolean = torchEnabled

    /** Whether the active camera source reports a flash unit. */
    fun isTorchAvailable(): Boolean = flashAvailable

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

        val videoImageReader = ImageReader.newInstance(format.width, format.height, ImageFormat.YUV_420_888, 2)
        videoImageReader.setOnImageAvailableListener(
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
        this.videoImageReader = videoImageReader

        val photoImageReader = ImageReader.newInstance(photoSize.width, photoSize.height, ImageFormat.JPEG, 3)
        photoImageReader.setOnImageAvailableListener(
            { reader ->
                val previousImage = latestImage
                val newImage = reader.acquireLatestImage()
                if (newImage != null) {
                    latestImage = newImage
                    previousImage?.close()
                }
            },
            imageReaderHandler
        )
        this.photoImageReader = photoImageReader

        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
        captureRequestBuilder.addTarget(videoImageReader.surface)
        captureRequestBuilder.addTarget(photoImageReader.surface)

        // + ensure AE is on and apply current torch state
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
        captureRequestBuilder.set(
            CaptureRequest.FLASH_MODE,
            if (torchEnabled && flashAvailable) CaptureRequest.FLASH_MODE_TORCH else CaptureRequest.FLASH_MODE_OFF
        )
        this.captureRequestBuilder = captureRequestBuilder

        @Suppress("DEPRECATION")
        cameraDevice.createCaptureSession(
            listOf(videoImageReader.surface, photoImageReader.surface),
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
        videoImageReader?.setOnImageAvailableListener(null, null)
        photoImageReader?.setOnImageAvailableListener(null, null)
        latestImage?.close()
        cameraCaptureSession?.stopRepeating()

        videoImageReader = null
        photoImageReader = null
        latestImage = null
        cameraCaptureSession = null
        captureRequestBuilder = null
    }

}
