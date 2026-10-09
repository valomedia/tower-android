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