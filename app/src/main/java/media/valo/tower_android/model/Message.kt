/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import media.valo.tower_android.utils.JsonPropertyClassDiscriminationSerializer

//
//  Message.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * A data channel message.
 */
@Serializable(with = MessageSerializer::class)
sealed class Message

/**
 * Serializer for data channel messages.
 */
object MessageSerializer: JsonContentPolymorphicSerializer<Message>(Message::class) {

    override fun selectDeserializer(element: JsonElement) = when {
        "error" in element.jsonObject -> JsonPropertyClassDiscriminationSerializer(ErrorMessage.serializer())
        else -> JsonPropertyClassDiscriminationSerializer(DataMessage.serializer())
    }

}

/**
 * A data channel message that is sent during normal operation.
 */
@Serializable
sealed class DataMessage: Message() {

    /**
     * A userHelloEvent data message.
     *
     * This is sent once when the call starts to transmit all the information the assistant needs
     * about the call.
     *
     * @param clientInfo    Information about the app the user is using to connect.
     * @param userProfile   Information about the user making the call.
     */
    @Serializable
    @SerialName("userHelloEvent")
    data class UserHelloEvent(
        val clientInfo: ClientInfo,
        val userProfile: UserProfile
    )

}

/**
 * A data channel message sent when something goes wrong.
 */
@Serializable
sealed class ErrorMessage: Message() {

    /**
     * A generic error event for unexpected errors.
     *
     * @param error             A message describing the error that occurred in English.
     * @param localizedError    A message describing the error that occurred in the language of the user, if available.
     */
    @Serializable
    @SerialName("errorEvent")
    data class ErrorEvent(
        val error: String,
        val localizedError: String?
    )

}
