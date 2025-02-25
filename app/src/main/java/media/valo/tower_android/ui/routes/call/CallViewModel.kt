/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.call

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.azure.android.communication.calling.AcceptCallOptions
import com.azure.android.communication.calling.Call
import com.azure.android.communication.calling.CallAgent
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.calling.CallState
import com.azure.android.communication.calling.CameraFacing
import com.azure.android.communication.calling.CreateViewOptions
import com.azure.android.communication.calling.DataChannelCallFeature
import com.azure.android.communication.calling.DataChannelPriority
import com.azure.android.communication.calling.DataChannelReceiver
import com.azure.android.communication.calling.DataChannelReceiverCreatedListener
import com.azure.android.communication.calling.DataChannelReliability
import com.azure.android.communication.calling.DataChannelSender
import com.azure.android.communication.calling.DataChannelSenderOptions
import com.azure.android.communication.calling.DeviceManager
import com.azure.android.communication.calling.Features
import com.azure.android.communication.calling.IncomingCall
import com.azure.android.communication.calling.LocalVideoStream
import com.azure.android.communication.calling.OutgoingVideoOptions
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.calling.ScalingMode
import com.azure.android.communication.calling.VideoDeviceInfo
import com.azure.android.communication.calling.VideoStreamRenderer
import com.azure.android.communication.common.CommunicationTokenCredential
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.SettingsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import media.valo.tower_android.R
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.model.AssistanceSessionState
import media.valo.tower_android.model.DataMessage
import media.valo.tower_android.model.ErrorMessage
import media.valo.tower_android.model.Message
import media.valo.tower_android.utils.AppScope
import javax.inject.Inject

//
//  CallViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//      * mvlexs
//

/**
 * The id for the data channel everything except photos is transmitted over
 */
private const val DURABLE_DATA_CHANNEL_ID = 1000

/**
 * The bandwidth for the data channel everything except photos is transmitted over.
 */
private const val DURABLE_DATA_CHANNEL_BANDWIDTH_KBPS = 32

/**
 * How long to wait before resending a message that failed to send.
 *
 * We will only retry sending messages on the durable data channel. Those will be resent on a loop
 * until it finally works. Since the messages are small, it's unlikely the resends would ever
 * accumulate to the point where that becomes a problem, and if they do, other issues will have
 * rendered the call unrecoverably broken before then anyways.
 */
private const val DATA_CHANNEL_RETRY_SEND_DELAY_MILLIS = 2000L

/**
 * How many seconds to wait before establishing the data channel after the call connects.
 *
 * Data channels are a new feature in Azure Communication Services and are still quite brittle. To
 * reduce the likelihood of data channel establishment failing, we add a little bit of a delay
 * between the call connecting and the data channel being established.
 */
private const val DATA_CHANNEL_ESTABLISH_DELAY_MILLIS = 1000L

/**
 * How long to wait between messages when sending multiple messages through the durable channel.
 *
 * There are situations where multiple data channel messages might need to be sent out in response
 * to a single event (such as the call connecting). Because the data channel implementation in
 * Azure Communication Services is new and still a little bit brittle, we add a small delay between
 * messages (and before the first message send after establishing the data channel), to reduce the
 * likelihood of ACS freaking out and starting to hurl exceptions our way.
 */
private const val DATA_CHANNEL_MESSAGE_BURST_DELAY_MILLIS = 1000L

/**
 * How often to update the user's location, in milliseconds.
 *
 * The system will do its best to provide an update on the location of the user at least this often.
 */
private const val LOCATION_UPDATE_INTERVAL_MILLIS = 20000L

/**
 * How often to update the user's location at most, in milliseconds.
 *
 * This is the fastest rate at which the system will update the user's location, if it is deciding
 * to provide more updates than absolutely necessary, because the system is in a state where the
 * additional updates will not greatly impact battery life.
 */
private const val LOCATION_UPDATE_MIN_INTERVAL_MILLIS = 10000L

/**
 * Tag added to log messages related to the CallViewModel.
 */
private const val TAG = "CallViewModel"

/**
 * `ViewModel` for `CallScreen`.
 *
 * @param towerRepository                   `TowerRepository` dependency.
 * @param profileRepository                 `ProfileRepository` dependency.
 * @param json                              `Json` dependency.
 * @param context                           `Context` dependency.
 * @param fusedLocationClient               `FusedLocationProviderClient` dependency.
 * @param locationServicesSettingsClient    `SettingsClient` dependency.
 * @param looper                            `Looper` dependency.
 * @param appScope                          `AppScope` dependency.
 */
@HiltViewModel
class CallViewModel @Inject constructor(
    private val towerRepository: TowerRepository,
    private val profileRepository: ProfileRepository,
    private val json: Json,
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationServicesSettingsClient: SettingsClient,
    private val looper: Looper,
    @AppScope val appScope: CoroutineScope
): ViewModel() {

    private val locationRequest = LocationRequest
        .Builder(LOCATION_UPDATE_INTERVAL_MILLIS)
        .setMinUpdateIntervalMillis(LOCATION_UPDATE_MIN_INTERVAL_MILLIS)
        .setPriority(PRIORITY_HIGH_ACCURACY)
        .build()

    private val locationSettingsRequestBuilder: LocationSettingsRequest.Builder =
        LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

    /**
     * The state the assistance session is in.
     *
     * This gives a high-level overview of the lifecycle of the call.
     */
    var sessionState by mutableStateOf(AssistanceSessionState.DISCONNECTED)

    /**
     * Whether the assistant has requested the user's location.
     *
     * This is true, if location has been requested by the assistant, but location data is not (yet)
     * being sent.
     */
    var isRequestingLocationUpdates by mutableStateOf(false)

    private var callClient: CallClient? = null

    private var callAgent: CallAgent? = null

    private var deviceManager: DeviceManager? = null

    private var audioManager: AudioManager? = null

    private var call: Call? = null

    private var currentCamera: VideoDeviceInfo? = null

    private var currentVideoStream: LocalVideoStream? = null

    private var previewRenderer: VideoStreamRenderer? = null

    private var onCallError: () -> Unit = {}

    private var startSound: MediaPlayer? = null

    private var ringbackSound: MediaPlayer? = null

    private var endSound: MediaPlayer? = null

    private var errorSound: MediaPlayer? = null

    private var dataChannelCallFeature: DataChannelCallFeature? = null

    private var dataChannelSender: DataChannelSender? = null

    private var dataChannelReceiver: DataChannelReceiver? = null

    /**
     * Start the assistance session.
     *
     * This will connect to the backend to create a new assistance session, use the token from the
     * backend to connect to ACS, and register a callback for when the assistant connects.
     *
     * @param onCallError   Callback to invoke if establishing the call fails.
     */
    fun startSession(onCallError: (() -> Unit) = {}) {
        sessionState = AssistanceSessionState.INITIALIZING
        this.onCallError = onCallError

        configureAudio()
        ringbackSound?.start()

        appScope.launch {
            try {
                createAgent(createSession()).addOnIncomingCallListener { incomingCall ->
                    appScope.launch { handleIncomingCall(incomingCall) }
                }
                sessionState = AssistanceSessionState.WAITING
            } catch (_: Exception) {
                onCallError()
                disposeSession()
                ringbackSound?.pause()
                errorSound?.start()
            }
        }
    }

    /**
     * End the assistance session.
     *
     * This will hang up the call, if the user is already talking to an assistant, or cancel the
     * assistance request, if the user is still waiting to be served.
     */
    fun endSession() {
        val call = this.call
        if (call != null) {
            call.hangUp()
        } else {
            if (sessionState == AssistanceSessionState.WAITING) {
                // Tell the backend we're gone. It's ok if this fails, the backend will notice on
                // its own eventually.
                appScope.launch {
                    try { towerRepository.cancelAssistance() } catch (_: Exception) { }
                }
            }

            disposeSession()
            ringbackSound?.pause()
        }
    }

    /**
     * Switch cameras.
     *
     * This will switch to the next available camera. If the user isn't on a call, or the camera
     * isn't active, or no other camera is available, this will do nothing.
     */
    fun switchSource() {
        currentVideoStream?.switchSource(getNextAvailableCamera())
    }

    /**
     * Start rendering the preview for the video stream.
     *
     * @param activity  The current Activity, used to access the ui thread.
     * @param container The ViewGroup to render the preview into.
     */
    fun showPreview(activity: Activity, container: ViewGroup) {
        if (currentVideoStream == null) { return }
        previewRenderer?.dispose()
        val previewRenderer = VideoStreamRenderer(currentVideoStream, activity)
        this.previewRenderer = previewRenderer

        val preview = previewRenderer.createView(CreateViewOptions(ScalingMode.FIT))
        preview?.tag = 0

        activity.runOnUiThread {
            container.addView(preview)
        }
    }

    /**
     * Send a Message through the durable data channel.
     *
     * @param message The Message to send.
     */
    fun sendMessage(message: Message) = dataChannelSender?.sendMessage(
        when (message) {
            is DataMessage -> json.encodeToString(message)
            is ErrorMessage -> json.encodeToString(message)
        }
            .toByteArray(Charsets.UTF_8)
    )

    private fun configureAudio() {
        val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        this.audioManager = audioManager

        val audioAttributes = AudioAttributes
            .Builder()
            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val audioSessionId = audioManager.generateAudioSessionId()

        val ringbackSound = MediaPlayer
            .create(context, R.raw.call_ringback_tone, audioAttributes, audioSessionId)
            .apply { isLooping = true }
        val startSound = MediaPlayer.create(context, R.raw.call_start_tone, audioAttributes, audioSessionId)
        val endSound = MediaPlayer.create(context, R.raw.call_end_tone, audioAttributes, audioSessionId)
        val errorSound = MediaPlayer.create(context, R.raw.call_error_tone, audioAttributes, audioSessionId)

        this.ringbackSound = ringbackSound
        this.startSound = startSound
        this.endSound = endSound
        this.errorSound = errorSound
    }

    private suspend fun createSession(): CommunicationTokenCredential {
        val requestAssistanceResponse = towerRepository.requestAssistance()
        if (requestAssistanceResponse.keepaliveInterval != null) {
            this.appScope.launch { sendKeepalives(requestAssistanceResponse.keepaliveInterval) }
        }
        return CommunicationTokenCredential(requestAssistanceResponse.userToken.token)
    }

    private suspend fun sendKeepalives(keepAliveInterval: Int) {
        val keepAliveIntervalMillis = keepAliveInterval * 1000L
        delay(keepAliveIntervalMillis)
        while (this.sessionState == AssistanceSessionState.WAITING) {
            try {
                towerRepository.awaitAssistance()
            } catch (_: Exception) {
                // Got an error updating the request. This might be because this assistant has
                // already accepted the request and is still in the process of picking up though, so
                // give it a little time.
                delay(keepAliveIntervalMillis)
                if (this.sessionState == AssistanceSessionState.WAITING) {
                    // If we still haven't heard from the assistant by now, we probably have
                    // a connection issue.
                    onCallError()
                    disposeSession()
                    this.ringbackSound?.pause()
                    this.errorSound?.start()
                }
            }
            delay(keepAliveIntervalMillis)
        }
    }

    private fun createAgent(credential: CommunicationTokenCredential): CallAgent {
        val callClient = CallClient()
        val callAgent = callClient.createCallAgent(context, credential).get()
        val deviceManager = callClient.getDeviceManager(context).get()
        this.callClient = callClient
        this.callAgent = callAgent
        this.deviceManager = deviceManager
        return callAgent
    }

    private fun handleIncomingCall(incomingCall: IncomingCall) {
        sessionState = AssistanceSessionState.CONNECTING
        currentCamera = getCameraFacing(CameraFacing.BACK)
        currentVideoStream = LocalVideoStream(currentCamera, context)
        val acceptCallOptions = AcceptCallOptions()
        val outgoingVideoOptions = OutgoingVideoOptions()
        outgoingVideoOptions.setOutgoingVideoStreams(listOf(currentVideoStream))
        acceptCallOptions.outgoingVideoOptions = outgoingVideoOptions
        ringbackSound?.pause()
        try {
            call = incomingCall.accept(context, acceptCallOptions).get()
        } catch (_: Exception) {
            incomingCall.reject()
            disposeSession()
            onCallError()
            this.errorSound?.start()
        }

        // Switch to speakerphone if possible.
        val speakerDevice = audioManager
            ?.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            ?.find { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
        if (speakerDevice != null) {
            audioManager?.setCommunicationDevice(speakerDevice)
        }

        call?.addOnStateChangedListener { handleCallOnStateChanged() }
    }

    private fun handleCallOnStateChanged() {
        when (call?.state) {
            CallState.CONNECTED -> handleCallConnected()
            CallState.DISCONNECTED -> handleCallDisconnected()
            else -> {}
        }
    }

    private fun handleCallConnected() {
        sessionState = AssistanceSessionState.CONNECTED
        this.startSound?.start()
        this.appScope.launch { establishDataChannel() }
    }

    private fun handleCallDisconnected() {
        val callEndCode = call?.callEndReason?.code
        val errorRange = 400..699
        disposeSession()
        if ( errorRange.contains(callEndCode) ){
            this.errorSound?.start()
        }
        else{
            this.endSound?.start()
        }
    }

    private fun disposeSession() {
        callClient?.dispose()
        callAgent?.dispose()
        previewRenderer?.dispose()

        callClient = null
        callAgent = null
        deviceManager = null
        audioManager = null
        call = null
        currentCamera = null
        currentVideoStream = null
        previewRenderer = null
        dataChannelCallFeature = null
        dataChannelSender = null
        dataChannelReceiver = null

        sessionState = AssistanceSessionState.DISCONNECTED
        isRequestingLocationUpdates = false
    }

    private fun getNextAvailableCamera(): VideoDeviceInfo? {
        val availableCameras = deviceManager?.cameras ?: emptyList<VideoDeviceInfo>()
        if (availableCameras.isEmpty()) {
            return null
        }
        for (i in availableCameras.indices) {
            if (currentCamera?.id == availableCameras[i].id) {
                return availableCameras[(i + 1) % availableCameras.size]
            }
        }
        return availableCameras[0]
    }

    private fun getCameraFacing(@Suppress("SameParameterValue") cameraFacing: CameraFacing): VideoDeviceInfo? {
        return deviceManager?.cameras?.first { it.cameraFacing == cameraFacing }
    }

    private suspend fun establishDataChannel() {
        val call = call
        if (call == null) { return }
        delay(DATA_CHANNEL_ESTABLISH_DELAY_MILLIS)

        val dataChannelCallFeature = call.feature(Features.DATA_CHANNEL)
        this.dataChannelCallFeature = dataChannelCallFeature

        val dataChannelReceiverCreatedListener = DataChannelReceiverCreatedListener {
            this.dataChannelReceiver = it.receiver
            it.receiver.addOnMessageReceivedListener(PropertyChangedListener {
                val data = dataChannelReceiver?.receiveMessage()?.data
                if (data == null) {
                    return@PropertyChangedListener
                }

                val message = try {
                    json.decodeFromString<Message>(String(data))
                } catch (e: Exception) {
                    Log.v(TAG, "Got a data channel message that is not understood by this client", e)
                    return@PropertyChangedListener
                }

                when (message) {
                    is DataMessage.LocationRequest -> handleLocationRequest()
                    else -> {}
                }
            })
        }
        dataChannelCallFeature.addOnReceiverCreatedListener(dataChannelReceiverCreatedListener)

        val dataChannelSenderOptions = DataChannelSenderOptions()
        dataChannelSenderOptions.channelId = DURABLE_DATA_CHANNEL_ID
        dataChannelSenderOptions.setPriority(DataChannelPriority.HIGH)
        dataChannelSenderOptions.setReliability(DataChannelReliability.DURABLE)
        dataChannelSenderOptions.bitrateInKbps = DURABLE_DATA_CHANNEL_BANDWIDTH_KBPS

        val dataChannelSender = dataChannelCallFeature.getDataChannelSender(dataChannelSenderOptions)
        this.dataChannelSender = dataChannelSender

        delay(DATA_CHANNEL_MESSAGE_BURST_DELAY_MILLIS)
        sendMessage(DataMessage.UserHelloEvent(profileRepository.getUserProfile()))
    }

    private fun handleLocationRequest() {
        isRequestingLocationUpdates = true

        // In Android we can't really know whether the user has denied location permissions. We only
        // know whether we currently have location permissions, but if we don't, it's completely
        // impossible to know whether this is because the user has denied location access, or
        // whether the user has just not been asked yet. Therefore we can't ever confidently say
        // that location isn't available and will not be available (which would allow us to disable
        // the button for the assistant). Instead we will always send a normal response, causing the
        // button to be re-enabled for the assistant, even though we don't know whether there is any
        // point in pushing it again.
        sendMessage(DataMessage.LocationResponse())
    }

}
