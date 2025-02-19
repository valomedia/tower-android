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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import media.valo.tower_android.R
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.model.AssistanceSessionState
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

private const val ASSISTANCE_REQUEST_KEEP_ALIVE_TIMEOUT_MILLIS = 30000L

/**
 * `ViewModel` for `CallScreen`.
 *
 * @param towerRepository   `TowerRepository` dependency.
 * @param appScope          `AppScope` dependency.
 */
@HiltViewModel
class CallViewModel @Inject constructor(
    private val towerRepository: TowerRepository,
    @AppScope val appScope: CoroutineScope
): ViewModel() {

    /**
     * The state the assistance session is in.
     *
     * This gives a high-level overview of the lifecycle of the call.
     */
    var sessionState by mutableStateOf(AssistanceSessionState.DISCONNECTED)

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

    private var dataChannelSenderOptions: DataChannelSenderOptions? = null

    private var dataChannelReceiver: DataChannelReceiver? = null

    private var dataChannelReceiverCreatedListener: DataChannelReceiverCreatedListener? = null

    private var dataChannelMessageReceivedListener: PropertyChangedListener? = null

    private var dataChannelReceiverClosedListener: PropertyChangedListener? = null

    /**
     * Start the assistance session.
     *
     * This will connect to the backend to create a new assistance session, use the token from the
     * backend to connect to ACS, and register a callback for when the assistant connects.
     *
     * @param context       The application context to use for access to things like camera and microphone.
     * @param onCallError   Callback to invoke if establishing the call fails.
     */
    fun startSession(context: Context, onCallError: (() -> Unit) = {}) {
        sessionState = AssistanceSessionState.INITIALIZING
        this.onCallError = onCallError

        configureAudio(context)
        ringbackSound?.start()

        appScope.launch {
            try {
                createAgent(context, createSession()).addOnIncomingCallListener { incomingCall ->
                    appScope.launch { handleIncomingCall(context, incomingCall) }
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

    private fun configureAudio(context: Context) {
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

    private fun createAgent(context: Context, credential: CommunicationTokenCredential): CallAgent {
        val callClient = CallClient()
        val callAgent = callClient.createCallAgent(context, credential).get()
        val deviceManager = callClient.getDeviceManager(context).get()
        this.callClient = callClient
        this.callAgent = callAgent
        this.deviceManager = deviceManager
        return callAgent
    }

    private fun handleIncomingCall(
        context: Context,
        incomingCall: IncomingCall
    ) {
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
        initializeDataChannel()
        sendUserHelloEvent()
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

        sessionState = AssistanceSessionState.DISCONNECTED
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

    private fun initializeDataChannel(){

        //enable data channel feature for our current call object
        dataChannelCallFeature = call?.feature(Features.DATA_CHANNEL)

        //define listener for creation of dataChannelReceiver
        dataChannelReceiverCreatedListener = DataChannelReceiverCreatedListener { receiverCreated ->
            dataChannelReceiver = receiverCreated.receiver
            val channelId = dataChannelReceiver?.channelId
            val senderId = dataChannelReceiver?.senderIdentifier
        }

        //define listener for receiving messages
        dataChannelMessageReceivedListener = PropertyChangedListener { messageReceived ->
            val message = dataChannelReceiver?.receiveMessage()
            val sequence = message?.sequenceNumber
            val data = message?.data
        }

        //define listener for closing our receiver
        dataChannelReceiverClosedListener = PropertyChangedListener { receiverClosed ->
            val receiver = dataChannelReceiver
        }

        //pass the receivers that have been defined above to our dataChannel object
        dataChannelCallFeature?.addOnReceiverCreatedListener(dataChannelReceiverCreatedListener)
        dataChannelReceiver?.addOnMessageReceivedListener(dataChannelMessageReceivedListener)
        dataChannelReceiver?.addOnClosedListener(dataChannelReceiverClosedListener)

        //specify the options for the data channel sender
        dataChannelSenderOptions?.setChannelId(1000)
        dataChannelSenderOptions?.setPriority(DataChannelPriority.HIGH)
        dataChannelSenderOptions?.setReliability(DataChannelReliability.DURABLE)
        dataChannelSenderOptions?.setBitrateInKbps(32)

        //create the data channel sender and apply set options
        dataChannelSender = dataChannelCallFeature?.getDataChannelSender(dataChannelSenderOptions)

    }

    private fun sendUserHelloEvent() {

        val userHello: ByteArray? = null
        //TODO UserHello -> Byte Array to send to Backend
        dataChannelSender?.sendMessage(userHello)
    }
}
