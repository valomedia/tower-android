/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.call

import android.content.Context
import android.media.MediaPlayer

//
//  CallSounds.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

class CallSound {

    private var mediaPlayer: MediaPlayer? = null

    val context: Context

    /**
     *
     * Plays the requested sound.
     * @param soundId The id of the sound to play.
     *
     */
    constructor(context: Context, soundId: Int, shouldLoop: Boolean = false) {
        this.context = context
        mediaPlayer = MediaPlayer.create(context, soundId).apply {
            isLooping = shouldLoop

            setOnCompletionListener {
                mediaPlayer = null
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
        mediaPlayer?.stop()
        mediaPlayer = null
    }
}
