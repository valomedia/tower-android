/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.call

import android.util.Log
import com.azure.android.communication.calling.RawOutgoingVideoStream
import com.azure.android.communication.calling.RawOutgoingVideoStreamOptions
import com.azure.android.communication.calling.VideoStreamState
import com.azure.android.communication.calling.VirtualOutgoingVideoStream
import media.valo.tower_android.data.local.video.VideoRepository
import media.valo.tower_android.model.CallQualityLevel

//
//  VideoFrameSender.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

private const val TAG = "VideoFrameSender"

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
                VideoStreamState.STOPPED -> stop()
                else -> Unit
            }
        }
        virtualOutgoingVideoStream
    }

    private fun start() {
        Log.d(TAG, "Video stream started")
        videoRepository.start(rawOutgoingVideoStream::sendRawVideoFrame)
    }

    private fun stop() {
        Log.d(TAG, "Video stream stopped")
        videoRepository.stop()
    }

}