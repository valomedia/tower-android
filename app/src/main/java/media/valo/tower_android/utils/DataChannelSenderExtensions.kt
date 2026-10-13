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

import com.azure.android.communication.calling.DataChannelSender
import kotlinx.serialization.encodeToString
import media.valo.tower_android.model.DataMessage
import media.valo.tower_android.model.ErrorMessage
import media.valo.tower_android.model.Message

//
//  DataChannelSenderExtensions.kt
//  Tower_Android
//

private const val TAG = "VideoFrameSender"

val json = JsonModule().provideJson()

/**
 * Send a Message through the data channel.
 *
 * @param message The Message to send.
 */
fun DataChannelSender.sendMessage(message: Message) {
    sendMessage(
        when (message) {
            is DataMessage -> json.encodeToString(message)
            is ErrorMessage -> json.encodeToString(message)
        }
            .toByteArray(Charsets.UTF_8)
    )
}
