/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.call

import android.content.Context
import android.media.MediaPlayer
import media.valo.tower_android.R

//
//  CallSounds.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

class CallSound(val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    /**
     *
     * Plays the requested sound.
     * @param soundId The id of the sound to play.
     *
     */
    fun play(soundId: Int) {
        releaseMediaPlayer()

        mediaPlayer = MediaPlayer.create(context, soundId).apply {
            isLooping = soundId == R.raw.call_ringback_tone

            setOnCompletionListener {
                releaseMediaPlayer()
            }
            start()
        }
    }

    /**
     *
     * Stops the currently playing sound and releases the MediaPlayer.
     *
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
     *
     * Releases the MediaPlayer resources.
     *
     */
    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
