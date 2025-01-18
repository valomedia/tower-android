/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/



package media.valo.tower_android.ui.routes.call_sounds

import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import media.valo.tower_android.R

//
//  CallSounds.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

class CallSounds(context: Context,requestedSound: String){

    private var resourceId = identifyRequest(requestedSound)

    private val requestedUri: Uri =  Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.packageName)
        .appendPath(resourceId.toString())
        .build()

    private val mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING)
                .build()
        )
        try {
            setDataSource(context, requestedUri)
            prepare()
            Log.d("CallSounds", "MediaPlayer prepared successfully")
        } catch (e: Exception) {
            Log.e("CallSounds", "Error preparing MediaPlayer", e)
        }
    }

    private fun identifyRequest(requestedSound: String): Int {
        return when (requestedSound) {
            "end" -> R.raw.call_end_tone
            "error" -> R.raw.call_error_tone
            "ringback" -> R.raw.call_ringback_tone
            "start" -> R.raw.call_start_tone
            else -> throw IllegalArgumentException("Invalid sound requested")
        }
    }

    fun play() {
        try {
            mediaPlayer.start()
            Log.d("CallSounds", "MediaPlayer started")
        } catch (e: Exception) {
            Log.e("CallSounds", "Error starting MediaPlayer", e)
        }
    }
}
