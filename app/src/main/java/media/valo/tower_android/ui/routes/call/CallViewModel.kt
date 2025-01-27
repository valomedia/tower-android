/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.call

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
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
import com.azure.android.communication.calling.DeviceManager
import com.azure.android.communication.calling.IncomingCall
import com.azure.android.communication.calling.LocalVideoStream
import com.azure.android.communication.calling.OutgoingVideoOptions
import com.azure.android.communication.calling.VideoDeviceInfo
import com.azure.android.communication.common.CommunicationTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private var onCallError: () -> Unit = {}

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

        appScope.launch {
            try {
                createAgent(context, createSession()).addOnIncomingCallListener { incomingCall ->
                    appScope.launch { handleIncomingCall(context, incomingCall) }
                }
                sessionState = AssistanceSessionState.WAITING
            } catch (_: Exception) {
                onCallError()
                disposeSession()
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
                }
            }
            delay(keepAliveIntervalMillis)
        }
    }

    private fun createAgent(context: Context, credential: CommunicationTokenCredential): CallAgent {
        val callClient = CallClient()
        val callAgent = callClient.createCallAgent(context, credential).get()
        val deviceManager = callClient.getDeviceManager(context).get()
        val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        this.callClient = callClient
        this.callAgent = callAgent
        this.deviceManager = deviceManager
        this.audioManager = audioManager
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
        try {
            call = incomingCall.accept(context, acceptCallOptions).get()
        } catch (_: Exception) {
            incomingCall.reject()
            disposeSession()
            onCallError()
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
    }

    private fun handleCallDisconnected() {
        disposeSession()
    }

    private fun disposeSession() {
        callClient?.dispose()
        callAgent?.dispose()

        callClient = null
        callAgent = null
        deviceManager = null
        audioManager = null
        call = null
        currentCamera = null
        currentVideoStream = null

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

}
