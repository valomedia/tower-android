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

import android.util.Size
import com.azure.android.communication.calling.RawVideoFrameBuffer
import com.azure.android.communication.calling.VideoStreamFormat
import javax.inject.Inject

//
//  VideoRepository.kt
//  Tower_Android
//

/**
 * A repository for video streams.
 *
 * @param videoDataSource   `VideoDataSource` dependency.
 */
class VideoRepository @Inject constructor(
    private val videoDataSource: VideoDataSource
) {

    /**
     * The format in which to provide the video frames.
     *
     * This will be used to set the with, height, pixel format and frame rate for the video frames produced. The
     * callback, will be invoked with frames in the desired format, at the desired frame rate. When changing formats,
     * the frames may not reflect the new format immediately. The caller should discard any frames that it can't use.
     * The frame rate is best effort. If the source can't produce frames at the desired rate, the callback may be
     * invoked at a different rate.
     */
    var format: VideoStreamFormat? by videoDataSource::format

    /**
     * Whether any preview of the video should be mirrored.
     *
     * If this property is true, the video from the source should be mirrored when shown to the user. Typically this
     * is the case, because the video is coming from a selfie-camera.
     */
    val shouldMirrorPreview: Boolean by videoDataSource::shouldMirrorPreview

    /**
     * The size of photos that will be output by `takePhoto()`.
     *
     * If the source can provide high-resolution still images, this will contain the resolution these images will have.
     * Otherwise this will contain a `Size` of zero by zero pixels.
     */
    val photoSize: Size by videoDataSource::photoSize

    /**
     * Start producing video frames.
     *
     * Once this is called, the `callback` provided will be continuously invoked with new video frames from the source,
     * as long as the source is able to produce video frames. The `format` for the video stream can be changed at any
     * time without having to call `start()` again. This function can be called again while the `VideoDataSource` is
     * already running to restart the source with the new callback.
     *
     * @param callback The callback to invoke whenever a new video frame is available.
     */
    fun start(callback: (RawVideoFrameBuffer) -> Unit) = videoDataSource.start(callback)

    /**
     * Stop producing video frames.
     *
     * When this is called, the video data source may stop producing new frames. To resume production of video frames,
     * `start()` may be called again.
     */
    fun stop() = videoDataSource.stop()

    /**
     * Switch to the next video feed.
     *
     * If the video source has multiple video feeds available, this function will cycle through them. Which video
     * feeds are available and in what order they will be output is specific to each particular implementation.
     */
    fun switchSource() = videoDataSource.switchSource()

    /**
     * Provide a high-resolution image from the video feed.
     *
     * Most sources are able to output individual photos at a much higher resolution than continuous video frames. If
     * this is the case, this function can be called to retrieve a single frame with the resolution given by the
     * `photoSize` property. If the particular `VideoDataSource` doesn't implement this feature, or if no photo can be
     * provided at this time (for example because the source has just been started and hasn't had time to capture an
     * image yet), this will return `null`.
     *
     * @return A `Bitmap` containing the image from the video feed, if one can be produced.
     */
    fun takePhoto() = videoDataSource.takePhoto()

    /**
     * Calculate the amount the video needs to be rotated to be upright for a particular device orientation.
     *
     * If the images being output by the source need to be rotated, this will return the amount of degrees of clockwise
     * rotation that need to be applied to the image to turn it upright. Since this may be dependent on the orientation
     * of the device itself, relative to its natural orientation, the `orientation` of the device needs to be provided.
     *
     * @param orientation Degrees of clockwise rotation of the ui, relative to the devices natural orientation.
     *
     * @return Degrees of clockwise rotation to add to the video and photos from the source.
     */
    fun rotationFor(orientation: Int): Int = videoDataSource.rotationFor(orientation)

    /**
     * Enable or disable the device torch (flashlight) if available.
     *
     * @param enabled true to turn on, false to turn off
     */
    fun setTorchEnabled(enabled: Boolean) {
        val cameraSource = (videoDataSource as? CameraVideoDataSource)
        if (cameraSource == null) {
            if (enabled) { throw IllegalStateException("Torch not supported for this video source.") }
            return
        }
        if (enabled && !cameraSource.isTorchAvailable()) {
            throw IllegalStateException("Torch not available on this camera.")
        }
        cameraSource.setTorchEnabled(enabled)
    }

    /**
     * Whether the device torch is currently enabled.
     */
    fun isTorchEnabled(): Boolean =
        (videoDataSource as? CameraVideoDataSource)?.isTorchEnabled() ?: false

    /**
     * Whether the current camera source supports a torch.
     */
    fun isTorchAvailable(): Boolean =
        (videoDataSource as? CameraVideoDataSource)?.isTorchAvailable() ?: false

}
