/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.utils

import com.azure.android.communication.calling.DataChannelSender
import de.tower_assist.tower_android.model.DataMessage
import de.tower_assist.tower_android.model.ErrorMessage
import de.tower_assist.tower_android.model.Message

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
