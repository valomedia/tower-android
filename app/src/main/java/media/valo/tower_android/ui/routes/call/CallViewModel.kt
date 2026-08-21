/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.call

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.azure.android.communication.calling.AcceptCallOptions
import com.azure.android.communication.calling.Call
import com.azure.android.communication.calling.CallAgent
import com.azure.android.communication.calling.CallClient
import com.azure.android.communication.calling.CallState
import com.azure.android.communication.calling.CameraFacing
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
import com.azure.android.communication.calling.OutgoingVideoOptions
import com.azure.android.communication.calling.PropertyChangedListener
import com.azure.android.communication.calling.VideoDeviceInfo
import com.azure.android.communication.calling.VideoStreamRenderer
import com.azure.android.communication.common.CommunicationTokenCredential
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.SettingsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import media.valo.tower_android.R
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.local.video.VideoRepository
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.model.AssistanceSessionState
import media.valo.tower_android.model.AssistanceSessionStatus
import media.valo.tower_android.model.DataMessage
import media.valo.tower_android.model.Message
import media.valo.tower_android.utils.AppScope
import media.valo.tower_android.utils.sendMessage
import javax.inject.Inject

//
//  CallViewModel.kt
//  Tower_Android
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
    private val videoRepository: VideoRepository,
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

    private var sessionStatus by mutableStateOf(
        AssistanceSessionStatus.of(AssistanceSessionState.DISCONNECTED)
    )

    /**
     * The state the assistance session is in.
     *
     * This gives a high-level overview of the lifecycle of the call.
     */
    val sessionState: AssistanceSessionState
        get() = sessionStatus.state

    /**
     * The user-facing status message for the current assistance session.
     */
    val sessionStatusMessage: String
        get() = sessionStatus.displayString

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

    private var videoFrameSender: VideoFrameSender? = null

    private var previewRenderer: VideoStreamRenderer? = null

    private var onCallError: () -> Unit = {}

    private var startSound: MediaPlayer? = null

    private var ringbackSound: MediaPlayer? = null

    private var endSound: MediaPlayer? = null

    private var errorSound: MediaPlayer? = null

    private var shutterSound: MediaPlayer? = null

    private var cameraSwitchSound: MediaPlayer? = null

    private var dataChannelCallFeature: DataChannelCallFeature? = null

    private var dataChannelSender: DataChannelSender? = null

    private var dataChannelReceiver: DataChannelReceiver? = null

    private var cameraFacingUser = false

    private var sessionGeneration: Long = 0L

    private var startSessionJob: Job? = null

    private var keepaliveJob: Job? = null

    /**
     * Start the assistance session.
     *
     * This will connect to the backend to create a new assistance session, use the token from the
     * backend to connect to ACS, and register a callback for when the assistant connects.
     *
     * @param onCallError   Callback to invoke if establishing the call fails.
     */
    fun startSession(onCallError: (() -> Unit) = {}) {
        cancelSessionJobs()
        val sessionGeneration = nextSessionGeneration()

        sessionStatus = AssistanceSessionStatus.of(AssistanceSessionState.INITIALIZING)
        this.onCallError = onCallError

        configureAudio()
        ringbackSound?.start()

        startSessionJob = appScope.launch {
            try {
                val credential = createSession(sessionGeneration) ?: return@launch
                if (!isSessionActive(sessionGeneration)) { return@launch }

                val callAgent = createAgent(credential)
                if (!isSessionActive(sessionGeneration)) {
                    disposeSession()
                    return@launch
                }

                callAgent.addOnIncomingCallListener { incomingCall ->
                    if (!isSessionActive(sessionGeneration)) {
                        incomingCall.reject()
                        return@addOnIncomingCallListener
                    }
                    appScope.launch { handleIncomingCall(incomingCall, sessionGeneration) }
                }

                if (!isSessionActive(sessionGeneration)) { return@launch }
                sessionStatus = AssistanceSessionStatus.waiting()
            } catch (_: Exception) {
                if (isSessionActive(sessionGeneration)) {
                    onCallError()
                    disposeSession()
                    ringbackSound?.pause()
                    errorSound?.start()
                }
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
        invalidateSession()

        val call = this.call
        if (call != null) {
            call.hangUp()
        } else {
            if (sessionState == AssistanceSessionState.WAITING
                || sessionState == AssistanceSessionState.INITIALIZING) {
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
     * Start sending video frames.
     *
     * This will start the transmission of video frames to the server, along with the necessary
     * orientation events, and begin rendering the video preview in the given `previewContainer`.
     *
     * @param activity          The current Activity, used to access the ui thread.
     * @param previewContainer  The ViewGroup to render the preview into.
     */
    fun enableVideoFrameSender(activity: Activity, previewContainer: ViewGroup) {
        val videoFrameSender = videoFrameSender
        if (videoFrameSender == null) { return }
        val preview = videoFrameSender.enable(activity)
        activity.runOnUiThread { previewContainer.addView(preview) }
    }

    fun startLocationUpdates(onLocationSettingsChangeNeeded: (ResolvableApiException) -> Unit) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                location ?: return
                dataChannelSender?.sendMessage(DataMessage.LocationEvent(location))
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.wtf(TAG, "Function startLocationUpdates called despite missing permissions")
            return
        }

        locationServicesSettingsClient
            .checkLocationSettings(locationSettingsRequestBuilder.build())
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    onLocationSettingsChangeNeeded(exception)
                }
            }

        Log.d(TAG, "Starting to send location")
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, looper)
        isRequestingLocationUpdates = false
    }

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
        val shutterSound = MediaPlayer.create(context, R.raw.camera_shutter_tone, audioAttributes, audioSessionId)
        val cameraSwitchSound = MediaPlayer.create(context, R.raw.camera_switch_tone, audioAttributes, audioSessionId)

        this.ringbackSound = ringbackSound
        this.startSound = startSound
        this.endSound = endSound
        this.errorSound = errorSound
        this.shutterSound = shutterSound
        this.cameraSwitchSound = cameraSwitchSound
    }

    private suspend fun createSession(
        sessionGeneration: Long
    ): CommunicationTokenCredential? {
        val requestAssistanceResponse = towerRepository.requestAssistance()
        if (!isSessionActive(sessionGeneration)) {
            try { towerRepository.cancelAssistance() } catch (_: Exception) { }
            return null
        }
        if (requestAssistanceResponse.keepaliveInterval != null) {
            keepaliveJob?.cancel()
            keepaliveJob = this.appScope.launch {
                sendKeepalives(
                    requestAssistanceResponse.keepaliveInterval,
                    sessionGeneration
                )
            }
        }
        return CommunicationTokenCredential(requestAssistanceResponse.userToken.token)
    }

    private suspend fun sendKeepalives(keepAliveInterval: Int, sessionGeneration: Long) {
        val keepAliveIntervalMillis = keepAliveInterval * 1000L
        delay(keepAliveIntervalMillis)
        while (isSessionActive(sessionGeneration)
            && this.sessionState == AssistanceSessionState.WAITING) {
            try {
                val reportedQueuePosition = towerRepository.awaitAssistance().position
                if (!isSessionActive(sessionGeneration)
                    || this.sessionState != AssistanceSessionState.WAITING) {
                    return
                }
                this.sessionStatus = AssistanceSessionStatus.waiting(reportedQueuePosition)
            } catch (_: Exception) {
                // Got an error updating the request. This might be because this assistant has
                // already accepted the request and is still in the process of picking up though, so
                // give it a little time.
                delay(keepAliveIntervalMillis)
                if (!isSessionActive(sessionGeneration)
                    || this.sessionState != AssistanceSessionState.WAITING) {
                    return
                }
                // If we still haven't heard from the assistant by now, we probably have
                // a connection issue.
                onCallError()
                disposeSession()
                this.ringbackSound?.pause()
                this.errorSound?.start()
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

    private fun handleIncomingCall(incomingCall: IncomingCall, sessionGeneration: Long) {
        if (!isSessionActive(sessionGeneration)) {
            incomingCall.reject()
            return
        }

        sessionStatus = AssistanceSessionStatus.of(AssistanceSessionState.CONNECTING)
        currentCamera = getCameraFacing(CameraFacing.BACK)
        cameraFacingUser = false
        videoFrameSender = VideoFrameSender(
            videoRepository = videoRepository,
            appScope = appScope
        )
        val acceptCallOptions = AcceptCallOptions()
        val outgoingVideoOptions = OutgoingVideoOptions()
        outgoingVideoOptions.setOutgoingVideoStreams(listOf(videoFrameSender?.rawOutgoingVideoStream))
        acceptCallOptions.outgoingVideoOptions = outgoingVideoOptions
        ringbackSound?.pause()
        try {
            call = incomingCall.accept(context, acceptCallOptions).get()
        } catch (_: Exception) {
            incomingCall.reject()
            disposeSession()
            onCallError()
            this.errorSound?.start()
            return
        }

        if (!isSessionActive(sessionGeneration)) {
            call?.hangUp()
            disposeSession()
            return
        }

        // Switch to speakerphone if possible.
        val speakerDevice = audioManager
            ?.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            ?.find { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
        if (speakerDevice != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
        sessionStatus = AssistanceSessionStatus.of(AssistanceSessionState.CONNECTED)
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
        invalidateSession()

        callClient?.dispose()
        callAgent?.dispose()
        videoFrameSender?.disable()
        previewRenderer?.dispose()
        videoFrameSender?.turnTorchOff()

        callClient = null
        callAgent = null
        deviceManager = null
        audioManager = null
        call = null
        currentCamera = null
        videoFrameSender = null
        previewRenderer = null
        dataChannelCallFeature = null
        dataChannelSender = null
        dataChannelReceiver = null

        sessionStatus = AssistanceSessionStatus.of(AssistanceSessionState.DISCONNECTED)
        isRequestingLocationUpdates = false
    }

    private fun nextSessionGeneration(): Long {
        sessionGeneration += 1
        return sessionGeneration
    }

    private fun isSessionActive(sessionGeneration: Long): Boolean =
        this.sessionGeneration == sessionGeneration

    private fun cancelSessionJobs() {
        startSessionJob?.cancel()
        startSessionJob = null
        keepaliveJob?.cancel()
        keepaliveJob = null
    }

    private fun invalidateSession() {
        sessionGeneration += 1
        cancelSessionJobs()
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
                    is DataMessage.SwitchCameraRequest -> videoFrameSender?.handleSwitchCameraRequest()
                    is DataMessage.CapturePhotoRequest -> {
                        shutterSound?.start()
                        videoFrameSender?.handleCapturePhotoRequest(message)
                    }
                    is DataMessage.SwitchCameraRequest -> {
                        cameraSwitchSound?.start()
                        videoFrameSender?.handleSwitchCameraRequest()
                    }
                    is DataMessage.CapturePhotoRequest -> videoFrameSender?.handleCapturePhotoRequest(message)
                    is DataMessage.ToggleTorchRequest -> videoFrameSender?.handleToggleTorchRequest()
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
        videoFrameSender?.startSendingOrientationEvents(dataChannelSender)

        delay(DATA_CHANNEL_MESSAGE_BURST_DELAY_MILLIS)
        dataChannelSender?.sendMessage(DataMessage.UserHelloEvent(profileRepository.getUserProfile()))
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
        dataChannelSender?.sendMessage(DataMessage.LocationResponse())
    }

}
