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

/**
 * Sends video frames to the server, dispatches orientation events and renders a preview.
 *
 * After constructing this, add the `RawOutgoingVideoStream` provided by this to a call, and call `enable()` with an
 * `Activity` to use for rendering the preview and determining the correct rotation for the video. The order isn't
 * important here. You just need to `enable()` the `VideoFrameSender` at some point before the call times out waiting
 * for frames.
 *
 * @param videoRepository   The `VideoRepository` to pull the video from.
 */
class VideoFrameSender(
    private val videoRepository: VideoRepository
) {

    /**
     * The stream the video stream the video is sent to.
     *
     * This is constructed automatically, but needs to be attached to a call by the user.
     */
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

    /**
     * Start sending video using the given `Activity`.
     *
     * Once this is called (and the `rawOutgoingVideoStream` has started), the sender will start sending frames. The
     * `Activity` provided here is used for rendering the preview and to determine the orientation of the ui, so the
     * video can be rotated if necessary. This function can be called repeatedly to replace the `activity` as
     * necessitated by Activity recreation.
     *
     * @param activity The Activity to use for rendering the preview and determining user interface orientation.
     *
     * @return The SurfaceView the video preview will be rendered into.
     */
    fun enable(activity: Activity): SurfaceView {
        this.activity = activity
        val previewRenderer = VideoFrameRenderer(activity, ScalingMode.FIT)
        this.previewRenderer = previewRenderer
        return previewRenderer.view
    }

    /**
     * Stop sending video.
     *
     * This can be used to halt the video output, even if the video stream is still attached to a call.
     */
    fun disable() {
        this.activity = null
        this.previewRenderer = null
    }

    /**
     * Start sending orientation events using the given `dataChannelSender`.
     *
     * This can be used to provide a dataChannelSender to send orientation events to. Once this is provided (or when the
     * first frame is sent, whichever occurs sooner), an `orientationEvent` with the current orientation of the video
     * will be sent. After that, additional `orientationEvent`s will be sent automatically as needed.
     *
     * Should the data channel change for whatever reason, this can be called again with a new data channel sender. The
     * new sender will be used to replace the old one. Only one data channel sender will be active at a time. The
     * current orientation will be initially sent to the new sender once, and then updated as needed.
     */
    fun startSendingOrientationEvents(dataChannelSender: DataChannelSender) {
        this.dataChannelSender = dataChannelSender
        rotation = null
    }

    /**
     * Stop sending orientation events.
     */
    fun stopSendingOrientationEvents() {
        dataChannelSender = null
        rotation = null
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
        stopSendingOrientationEvents()
        previewRenderer = null
        activity = null
        videoRepository.stop()
    }

}
