/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.video

import android.util.Size
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

    val photoSize: Size by videoDataSource::photoSize

    fun start(callback: (RawVideoFrameBuffer) -> Unit) = videoDataSource.start(callback)

    fun stop() = videoDataSource.stop()

    fun switchSource() = videoDataSource.switchSource()

    fun takePhoto() = videoDataSource.takePhoto()

    fun rotationFor(orientation: Int): Int = videoDataSource.rotationFor(orientation)

}
