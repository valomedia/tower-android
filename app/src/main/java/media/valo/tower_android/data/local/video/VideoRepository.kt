/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.video

import com.azure.android.communication.calling.RawVideoFrameBuffer
import com.azure.android.communication.calling.VideoStreamFormat
import javax.inject.Inject

//
//  VideoRepository.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * A repository for video streams.
 *
 * @param videoDataSource   `VideoDataSource` dependency.
 */
class VideoRepository @Inject constructor(
    private val videoDataSource: VideoDataSource
) {

    var format: VideoStreamFormat? by videoDataSource::format

    val shouldMirrorPreview: Boolean by videoDataSource::shouldMirrorPreview

    fun start(callback: (RawVideoFrameBuffer) -> CompletableFuture<Void>) = videoDataSource.start {
        // If the format changes concurrently with the creation of a frame, the VideoDataSource might produce a frame
        // that doesn't match the expected format. In this case sending the frame will fail, which is expected. This
        // isn't a problem, since the next frame will likely send just fine, so we just ignore it here.
        try { callback(it).get() } catch (_: ExecutionException) {}
    }

    fun stop() = videoDataSource.stop()

    fun switchSource() = videoDataSource.switchSource()

    fun takePhoto() = videoDataSource.takePhoto()

    fun rotationFor(orientation: Int): Int = videoDataSource.rotationFor(orientation)
}
