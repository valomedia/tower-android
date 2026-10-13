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

package media.valo.tower_android.utils

import com.azure.android.communication.calling.VideoStreamFormat
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.ceil

//
//  VideoStreamFormatExtensions.kt
//  Tower_Android
//

/**
 * Allocate the buffers needed to produce a `RawVideoFrameBuffer` with this format.
 *
 * This will allocate buffers as needed for the `width`, `height`, and `pixelFormat` of this format. The returned list
 * may contain anywhere from one to three buffers, depending on the `pixelFormat`.
 *
 * @return A list of directly allocated `ByteBuffers` with the right sizes for frames of this format.
 */
fun VideoStreamFormat.allocateBuffers(): List<ByteBuffer> = pixelFormat.planeSizes.map { planeSize ->
    ByteBuffer.allocateDirect(ceil(width * height * planeSize).toInt()).order(ByteOrder.nativeOrder())
}
