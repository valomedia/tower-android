/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.call

import android.app.Activity
import android.util.Log
import android.view.SurfaceView
import com.azure.android.communication.calling.CallingCommunicationException
import com.azure.android.communication.calling.DataChannelSender
import com.azure.android.communication.calling.RawOutgoingVideoStream
import com.azure.android.communication.calling.RawOutgoingVideoStreamOptions
import com.azure.android.communication.calling.ScalingMode
import com.azure.android.communication.calling.VideoStreamState
import com.azure.android.communication.calling.VirtualOutgoingVideoStream
import media.valo.tower_android.data.local.video.VideoRepository
import media.valo.tower_android.model.CallQualityLevel
import media.valo.tower_android.model.DataMessage
import media.valo.tower_android.utils.sendMessage

//
//  VideoFrameSender.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

private const val TAG = "VideoFrameSender"
private const val ROTATION_STEP = 90

class VideoFrameSender(
    private val videoRepository: VideoRepository
) {

    val rawOutgoingVideoStream: RawOutgoingVideoStream = run {
        val rawOutgoingVideoStreamOptions = RawOutgoingVideoStreamOptions()
        rawOutgoingVideoStreamOptions.formats = CallQualityLevel.entries.map { it.videoStreamFormat }

        val virtualOutgoingVideoStream = VirtualOutgoingVideoStream(rawOutgoingVideoStreamOptions)
        virtualOutgoingVideoStream.addOnFormatChangedListener {
            Log.d(TAG, "Stream format changed: ${it.format.width}x${it.format.height}@${it.format.framesPerSecond}")
            videoRepository.format = it.format
        }
        virtualOutgoingVideoStream.addOnStateChangedListener {
            when (it.stream.state) {
                VideoStreamState.STARTED -> start()
                VideoStreamState.STOPPING -> stop()
                else -> Unit
            }
        }
        virtualOutgoingVideoStream
    }

    private var rotation: Int? = null
    private var previewRenderer: VideoFrameRenderer? = null
    private var dataChannelSender: DataChannelSender? = null
    private var activity: Activity? = null

    fun enable(activity: Activity): SurfaceView {
        this.activity = activity
        val previewRenderer = VideoFrameRenderer(activity, ScalingMode.FIT)
        this.previewRenderer = previewRenderer
        return previewRenderer.view
    }

    fun disable() {
        this.activity = null
        this.previewRenderer = null
    }
    
    fun startSendingOrientationEvents(dataChannelSender: DataChannelSender) {
        this.dataChannelSender = dataChannelSender
    }

    private fun start() {
        Log.d(TAG, "Video stream started")
        videoRepository.start { frame ->
            val activity = activity
            if (activity == null) { return@start }
            val rotation = videoRepository.rotationFor(activity.display.rotation * ROTATION_STEP)
            val dataChannelSender = dataChannelSender
            if (rotation != this.rotation && dataChannelSender != null) {
                try {
                    dataChannelSender.sendMessage(DataMessage.OrientationEvent(rotation))
                    this.rotation = rotation
                } catch (_: CallingCommunicationException) {}
            }

            // If the format changes concurrently with the creation of a frame, the VideoDataSource might produce a frame
            // that doesn't match the expected format. In this case sending the frame will fail, which is expected. This
            // isn't a problem, since the next frame will likely send just fine, so we just ignore it here.
            try {
                previewRenderer?.render(
                    rawVideoFrameBuffer = frame,
                    rotationAngle = rotation,
                    mirror = videoRepository.shouldMirrorPreview
                )
                rawOutgoingVideoStream.sendRawVideoFrame(frame).get()
            } catch (e: Exception) {
                Log.d(TAG, "Sending video frame failed", e)
            }
        }
    }

    private fun stop() {
        Log.d(TAG, "Video stream stopping")
        videoRepository.stop()
    }

}
