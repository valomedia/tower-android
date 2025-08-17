/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.video

import android.graphics.Bitmap
import android.util.Size
import com.azure.android.communication.calling.RawVideoFrameBuffer
import com.azure.android.communication.calling.VideoStreamFormat

//
//  VideoDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * A data source for a video stream.
 */
interface VideoDataSource {

    /**
     * The format in which to provide the video frames.
     *
     * This will be used to set the with, height, pixel format and frame rate for the video frames produced. The
     * `onVideoFrameAvailable` callback, will always be invoked with frames in the desired format, but the frame rate
     * is best effort. If the source can't produce frames at the desired rate, the callback may be invoked at a
     * different rate.
     */
    var format: VideoStreamFormat?

    val shouldMirrorPreview: Boolean

    val photoSize: Size

    /**
     * Start producing video frames.
     *
     * Once this is called, video data will be streamed into `videoFrameFlow`.
     */
    fun start(callback: ((RawVideoFrameBuffer) -> Unit))

    /**
     * Stop producing video frames.
     *
     * When this is called, the video data source may stop producing new frames. To resume production of video frames,
     * `start()` may be called again.
     */
    fun stop()

    fun switchSource()

    fun takePhoto(): Bitmap?

    fun rotationFor(orientation: Int): Int

}