/******************************************************************************
 * Copyright (c) 2024-2026 valo.media GmbH                                    *
 * All rights reserved.                                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Affero General Public License as             *
 * published by the Free Software Foundation, either version 3 of the         *
 * License, or (at your option) any later version.                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Affero General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Affero General Public License   *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package de.tower_assist.tower_android.model

//
//  AssistanceSessionState.kt
//  Tower_Android
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
 * The user-visible assistance session status.
 *
 * @property state          The lifecycle state of the assistance session.
 * @property queuePosition  The zero-indexed queue position, if the backend has reported it.
 */
class AssistanceSessionStatus private constructor(
    val state: AssistanceSessionState,
    val queuePosition: Int?
) {

    /**
     * The user-facing status message for the current assistance session.
     */
    val displayString: String
        get() = state.toDisplayString(queuePosition)

    companion object {

        /**
         * Creates a status for a state that does not include queue information.
         */
        fun of(state: AssistanceSessionState): AssistanceSessionStatus =
            AssistanceSessionStatus(state, null)

        /**
         * Creates a waiting status with the reported queue position, if known.
         */
        fun waiting(queuePosition: Int? = null): AssistanceSessionStatus =
            AssistanceSessionStatus(AssistanceSessionState.WAITING, queuePosition)

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
