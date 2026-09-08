/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

//
//  AssistanceSessionStateTest.kt
//  Tower_Android
//
//  Created by:
//      * Yatsar (Agent)
//

class AssistanceSessionStateTest {

    @Test
    fun `uses waiting message while queue position is unknown`() {
        assertEquals(
            "Warten auf Assistenz…",
            AssistanceSessionState.WAITING.toDisplayString(null)
        )
    }

    @Test
    fun `uses next in line message for queue position zero`() {
        assertEquals(
            "Wir sind gleich für dich da",
            AssistanceSessionState.WAITING.toDisplayString(0)
        )
    }

    @Test
    fun `uses singular queue message for one person ahead`() {
        assertEquals(
            "Eine Person vor dir",
            AssistanceSessionState.WAITING.toDisplayString(1)
        )
    }

    @Test
    fun `uses plural queue message for multiple people ahead`() {
        assertEquals(
            "3 Personen vor dir",
            AssistanceSessionState.WAITING.toDisplayString(3)
        )
    }

    @Test
    fun `ignores queue position outside waiting state`() {
        assertEquals(
            "Anrufaufbau…",
            AssistanceSessionState.CONNECTING.toDisplayString(3)
        )
    }

    @Test
    fun `non-waiting status does not keep queue position`() {
        val status = AssistanceSessionStatus.of(AssistanceSessionState.CONNECTING)

        assertEquals(AssistanceSessionState.CONNECTING, status.state)
        assertNull(status.queuePosition)
        assertEquals("Anrufaufbau…", status.displayString)
    }

    @Test
    fun `waiting status keeps queue position with state`() {
        val status = AssistanceSessionStatus.waiting(2)

        assertEquals(AssistanceSessionState.WAITING, status.state)
        assertEquals(2, status.queuePosition)
        assertEquals("2 Personen vor dir", status.displayString)
    }

}
