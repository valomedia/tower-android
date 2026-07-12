/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

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
