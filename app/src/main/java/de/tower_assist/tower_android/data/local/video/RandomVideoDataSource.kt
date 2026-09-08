/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.data.local.video

import android.graphics.Bitmap
import android.util.Size
import androidx.core.graphics.createBitmap
import com.azure.android.communication.calling.RawVideoFrameBuffer
import com.azure.android.communication.calling.VideoStreamFormat
import com.azure.android.communication.calling.VideoStreamPixelFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import de.tower_assist.tower_android.model.CallQualityLevel
import de.tower_assist.tower_android.utils.AppScope
import de.tower_assist.tower_android.utils.allocateBuffers
import de.tower_assist.tower_android.utils.planeSizes
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

//
//  RandomVideoDataSource.kt
//  Tower_Android
//

/**
 * A `VideoDataSource` that output random noise.
 *
 * @param appScope `AppScope` dependency.
 */
class RandomVideoDataSource @Inject constructor(
    @AppScope private val appScope: CoroutineScope
): VideoDataSource {

    override var format: VideoStreamFormat? = null

    override val shouldMirrorPreview = false

    override val photoSize: Size = Size(
        CallQualityLevel.VERY_HIGH.videoStreamFormat.width,
        CallQualityLevel.VERY_HIGH.videoStreamFormat.height
    )

    private var videoFrameIterator: Job? = null

    override fun start(callback: (RawVideoFrameBuffer) -> Unit) {
        if (videoFrameIterator != null) { stop() }
        videoFrameIterator = appScope.launch {
            while (true) {
                val format = format
                if (format != null) {
                    val buffers = format.allocateBuffers().map { buffer ->
                        buffer.put(Random.nextBytes(buffer.capacity()))
                        buffer.rewind()
                        buffer
                    }

                    val videoFrame = RawVideoFrameBuffer()
                    videoFrame.buffers = buffers
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

    override fun switchSource() = Unit

    override fun takePhoto(): Bitmap? {
        val buffer = ByteBuffer.allocateDirect(
            photoSize.width * photoSize.height * VideoStreamPixelFormat.RGBA.planeSizes[0].toInt()
        )
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(Random.nextBytes(buffer.capacity()))

        val bitmap = createBitmap(photoSize.width, photoSize.height)
        bitmap.copyPixelsFromBuffer(buffer)

        return bitmap
    }

    override fun rotationFor(orientation: Int): Int = 0

}