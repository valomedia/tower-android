/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

//
//  AssistanceSessionState.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * An enum representing the lifecycle of an assistance session.
 */
enum class AssistanceSessionState {

    INITIALIZING {
        override fun toString(): String = "Verbindung herstellen…"
    },

    WAITING {
        override fun toString(): String = "Warten auf Assistenz…"
    },

    CONNECTING {
        override fun toString(): String = "Anrufaufbau…"
    },

    CONNECTED {
        override fun toString(): String = "Verbunden"
    },

    DISCONNECTED {
        override fun toString(): String = "Verbindung getrennt"
    }

}

/**
 * Returns the user-facing status message for the current assistance session.
 *
 * @param queuePosition The zero-indexed queue position, if the backend has reported it.
 */
fun AssistanceSessionState.toDisplayString(queuePosition: Int?): String =
    when {
        this != AssistanceSessionState.WAITING || queuePosition == null -> toString()
        queuePosition == 0 -> "Wir sind gleich für dich da"
        queuePosition == 1 -> "Eine Person vor dir"
        else -> "$queuePosition Personen vor dir"
    }
