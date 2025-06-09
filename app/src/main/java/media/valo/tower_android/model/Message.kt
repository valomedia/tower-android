/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

import android.location.Location
import android.os.Build
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import media.valo.tower_android.utils.JsonPropertyClassDiscriminationSerializer
import media.valo.tower_android.utils.then

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
        "error" in element.jsonObject -> ErrorMessage.serializer()
        else -> DataMessage.serializer()
    }

}

/**
 * A data channel message that is sent during normal operation.
 */
@Serializable(with = DataMessageSerializer::class)
sealed class DataMessage: Message() {

    /**
     * A locationRequest data message.
     *
     * When this is received, the assistant wants to know the user's location and the app should
     * provide it, if possible.
     */
    @Serializable
    @SerialName("locationRequest")
    class LocationRequest(): DataMessage()

    /**
     * A locationResponse data message.
     *
     * This is sent once in reply to a locationRequest to confirm that the location request has
     * been received. Since Android does not really allow distinguishing between cases where
     * location might still come (because the user is being prompted), and cases where location will
     * never come (because the user has denied access), this is sent regardless of whether any
     * actual location can be produced.
     */
    @Serializable
    @SerialName("locationResponse")
    class LocationResponse(): DataMessage()

    /**
     * A locationEvent data message.
     *
     * This is sent repeatedly once the assistant has requested location access. It contains most
     * recent location known for the user to the accuracy the user has decided to share.
     *
     * @param coordinate            The geographical coordinate information.
     * @param altitude              The altitude above mean sea level, in meters.
     * @param horizontalAccuracy    The radius of uncertainty for the location, in meters.
     * @param verticalAccuracy      The estimated uncertainty of the altitude value, in meters.
     * @param course                The direction the device is traveling, in degrees north.
     * @param courseAccuracy        The uncertainty of the course value, in degrees.
     */
    @Serializable
    @SerialName("locationEvent")
    data class LocationEvent(
        val coordinate: Coordinate,
        val altitude: Double? = null,
        val horizontalAccuracy: Float? = null,
        val verticalAccuracy: Float? = null,
        val course: Float? = null,
        val courseAccuracy: Float? = null
    ): DataMessage() {

        /**
         * Build a locationEvent from a given Location.
         *
         * @param location The location to construct a locationEvent for.
         */
        constructor(location: Location): this(
            coordinate = Coordinate(location),
            altitude =
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                && location.hasMslAltitude()
            ) {
                location.mslAltitudeMeters
            } else {
                null
            },
            horizontalAccuracy = location.hasAccuracy() then location.accuracy,
            verticalAccuracy =
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                && location.hasMslAltitudeAccuracy()
            ) {
                location.mslAltitudeAccuracyMeters
            } else {
                null
            },
            course = location.hasBearing() then location.bearing,
            courseAccuracy = location.hasBearingAccuracy() then location.bearingAccuracyDegrees
        )

    }

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
        val userProfile: UserProfile,
        @Required val clientInfo: ClientInfo = ClientInfo()
    ): DataMessage()

    /**
     * A switchCameraRequest data message.
     *
     * When this is received, the assistant wants to switch the direction the user's camera is facing and the app should
     * change directions, if possible.
     */
    @Serializable
    @SerialName("switchCameraRequest")
    class SwitchCameraRequest(): DataMessage()

    /**
     * A switchCameraResponse data message.
     *
     * This message is sent by the app of the caller in response to a switchCameraRequest.
     * Unless something went wrong, its contents should be an empty object.
     */
    @Serializable
    @SerialName("switchCameraResponse")
    class SwitchCameraResponse(): DataMessage()

    /**
     * A orientationChanged data message.
     *
     * This message can be sent to tower-staff to indicate that the video being received must be rotated.
     * It specifies a rotationAngle of either 0, 90, 180, or 270 degrees. The video will then be rotated clockwise by the specified amount.
     * The rotation is set relative to the original orientation of the video, not to the last rotation set by another orientatonEvent.
     *
     * @param rotationAngle   The amount of degrees the users video feed has to be turned. (0, 90, 180, 270)
     */
    @Serializable
    @SerialName("orientationEvent")
    data class OrientationEvent(
        @Required val rotationAngle: Int
    ): DataMessage()
}

/**
 * Serializer for data channel messages sent during normal operation.
 */
object DataMessageSerializer:
    JsonPropertyClassDiscriminationSerializer<DataMessage>(PolymorphicSerializer(DataMessage::class))

/**
 * A data channel message sent when something goes wrong.
 */
@Serializable(with = ErrorMessageSerializer::class)
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
    ): ErrorMessage()

}

/**
 * Serializer for data channel messages sent when something goes wrong.
 */
object ErrorMessageSerializer:
    JsonPropertyClassDiscriminationSerializer<ErrorMessage>(PolymorphicSerializer(ErrorMessage::class))
