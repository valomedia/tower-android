/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/



package media.valo.tower_android.ui.routes.call

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import media.valo.tower_android.R

//
//  CallSounds.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

class CallSounds(val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    // Map sound names to their corresponding raw resource IDs
    private val soundResources = mapOf(
        "end" to R.raw.call_end_tone,
        "error" to R.raw.call_error_tone,
        "start" to R.raw.call_start_tone,
        "ringback" to R.raw.call_ringback_tone
    )

    // List of sounds that should loop, currently this only applies to ringback
    private val loopingSound = listOf("ringback")

    /**
     * Plays the requested sound.
     * @param soundName The name of the sound to play.
     */
    fun play(soundName: String) {

        val soundId = soundResources[soundName] ?: run {
            Log.e("CallSounds", "Sound not found: $soundName")
            return
        }


        mediaPlayer = MediaPlayer.create(context, soundId).apply {

            isLooping = soundName in loopingSound

            setOnCompletionListener {
                releaseMediaPlayer()
            }

            // Start playback
            start()
        }
    }

    /**
     * Stops the currently playing sound and releases the MediaPlayer.
     */
    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    /**
     * Releases the MediaPlayer resources.
     */
    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}