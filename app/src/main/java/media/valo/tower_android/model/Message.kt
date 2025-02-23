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
     * been executed successfully. This means that the user has either allowed access to the
     * location already, or is being prompted for it. It does not necessarily mean location data
     * will be sent, since the user might still deny access when prompted, or the location of the
     * device might simply not be available.
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
     * An error response indicating that the location is not available.
     *
     * This is sent if the location cannot be determined because the user has previously blocked
     * access to the location. If the user hasn't made a decision yet (and will this be prompted),
     * a normal locationResponse is sent instead.
     *
     * @param error             A message describing the error that occurred in English.
     * @param localizedError    A message describing the error that occurred in the language of the user, if available.
     */
    @Serializable
    @SerialName("locationResponse")
    data class LocationResponse(
        val error: String,
        val localizedError: String?
    ): ErrorMessage()

    /**
     * An error event indicating that location data isn't available even though it initially seemed like it might be.
     *
     * This can happen if the user is prompted for location access and then denies the prompt, of if
     * determining the location failed.
     *
     * @param error             A message describing the error that occurred in English.
     * @param localizedError    A message describing the error that occurred in the language of the user, if available.
     */
    @Serializable
    @SerialName("locationEvent")
    data class LocationEvent(
        val error: String,
        val localizedError: String?
    ): ErrorMessage()


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
