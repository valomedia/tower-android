/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

import com.azure.android.communication.calling.DataChannelSender
import kotlinx.serialization.encodeToString
import media.valo.tower_android.model.DataMessage
import media.valo.tower_android.model.ErrorMessage
import media.valo.tower_android.model.Message

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
