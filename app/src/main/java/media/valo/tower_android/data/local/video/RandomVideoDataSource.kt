/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.video

import com.azure.android.communication.calling.RawVideoFrame
import com.azure.android.communication.calling.RawVideoFrameBuffer
import com.azure.android.communication.calling.VideoStreamFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import media.valo.tower_android.utils.AppScope
import media.valo.tower_android.utils.allocateBuffers
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

//
//  RandomVideoDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

class RandomVideoDataSource @Inject constructor(
    @AppScope private val appScope: CoroutineScope
): VideoDataSource {

    override var format: VideoStreamFormat? = null

    private var videoFrameIterator: Job? = null

    override fun start(callback: (RawVideoFrame) -> Unit) {
        if (videoFrameIterator != null) { stop() }
        videoFrameIterator = appScope.launch {
            while (true) {
                val format = format
                if (format != null) {
                    val planes = format.allocateBuffers().map { buffer ->
                        buffer.put(Random.nextBytes(buffer.capacity()))
                        buffer.rewind()
                        buffer
                    }

                    val videoFrame = RawVideoFrameBuffer()
                    videoFrame.buffers = planes
                    videoFrame.streamFormat = format
                    videoFrame.use(callback)

                    delay((1.0 / format.framesPerSecond).seconds)
                } else {
                    // Don't have format yet, check again in a millisecond.
                    delay(1L)
                }
            }
        }
    }

    override fun stop() {
        runBlocking { videoFrameIterator?.cancelAndJoin() }
        videoFrameIterator = null
    }

}