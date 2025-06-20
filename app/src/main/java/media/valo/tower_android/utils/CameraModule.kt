
package media.valo.tower_android.utils

//
//  PreferencesDataStoreModule.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

import android.Manifest
import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.RequiresPermission
import com.azure.android.communication.calling.VirtualOutgoingVideoStream
class CameraModule(
    context: Context,
    val videoStream: VirtualOutgoingVideoStream?,
    val videoFrameSender: VideoFrameSender?
) {
    private var cameraDevice: CameraDevice? = null
    private var cameraId: String? = null
    private var cameraManager: CameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var imageReader: ImageReader? = null

    @RequiresPermission(Manifest.permission.CAMERA)
    fun startCamera(onCameraReady: (CameraDevice) -> Unit) {
        cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val lensFacing = characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
            lensFacing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_FRONT
        }
        if (cameraId == null) return

        startBackgroundThread()
        cameraManager.openCamera(cameraId!!, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                imageReader = ImageReader.newInstance(
                    videoStream?.format?.width ?: 640,
                    videoStream?.format?.height ?: 480,
                    android.graphics.ImageFormat.YUV_420_888,
                    1
                )
                imageReader?.setOnImageAvailableListener({ reader ->
                    val image = reader.acquireLatestImage()
                    if (image != null) {
                        videoFrameSender?.enqueueImage(image)
                    }
                }, backgroundHandler)
                onCameraReady(camera)

                val surface = imageReader!!.surface
                val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
                captureRequestBuilder.addTarget(surface)
                camera.createCaptureSession(
                    listOf(surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            session.setRepeatingRequest(
                                captureRequestBuilder.build(),
                                null,
                                backgroundHandler
                            )
                        }
                        override fun onConfigureFailed(session: CameraCaptureSession) {

                        }
                    },
                    backgroundHandler
                )
            }
            override fun onDisconnected(camera: CameraDevice) {
                camera.close()
                cameraDevice = null
            }
            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
                cameraDevice = null
            }
        }, backgroundHandler)
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    fun stopCamera() {
        cameraDevice?.close()
        cameraDevice = null
        backgroundThread?.quitSafely()
        backgroundThread = null
        backgroundHandler = null
    }
}
