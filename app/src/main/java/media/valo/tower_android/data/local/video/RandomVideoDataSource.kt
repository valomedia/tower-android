/******************************************************************************
 * Copyright (c) 2025-2026 valo.media GmbH                                    *
 * All rights reserved.                                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Affero General Public License as             *
 * published by the Free Software Foundation, either version 3 of the         *
 * License, or (at your option) any later version.                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Affero General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Affero General Public License   *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package media.valo.tower_android.data.local.video

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
import media.valo.tower_android.model.CallQualityLevel
import media.valo.tower_android.utils.AppScope
import media.valo.tower_android.utils.allocateBuffers
import media.valo.tower_android.utils.planeSizes
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