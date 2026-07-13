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
//  DummyVideoDataSource.kt
//  Tower_Android
//

/**
 * A dummy implementation of `VideoDataSource`.
 */
class DummyVideoDataSource : VideoDataSource {

    override var format: VideoStreamFormat? = null

    override val shouldMirrorPreview = false

    override val photoSize: Size = Size(0, 0)

    override fun start(callback: (RawVideoFrameBuffer) -> Unit) = Unit

    override fun stop() = Unit

    override fun switchSource() = Unit

    override fun takePhoto(): Bitmap? = null

    override fun rotationFor(orientation: Int): Int = 0

}