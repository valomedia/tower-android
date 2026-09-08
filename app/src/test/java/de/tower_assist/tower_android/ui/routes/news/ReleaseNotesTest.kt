/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.news

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

//
//  ReleaseNotesTest.kt
//  Tower_Android
//

/**
 * The notice about the service becoming chargeable, in the wording it was signed off in.
 */
private const val PRICING_NOTICE =
    "<b>Wichtige Info</b>: Der Fernassistenzservice wird bald kostenpflichtig. Fünf Minuten " +
            "Fernassistenz pro Monat werden weiterhin kostenlos sein. Zu den genauen Preisen " +
            "und Konditionen informieren wir dich über unsere üblichen Kanäle und natürlich " +
            "auch in der App."

/**
 * Tests for the release notes the app ships with.
 */
class ReleaseNotesTest {

    @Test
    fun `announces the pricing change on the release that carries it`() {
        val announcement = releaseNotes.single { it.changes.contains(PRICING_NOTICE) }

        assertEquals("1.2.0", announcement.version)
    }

    @Test
    fun `has something to say about every release it lists`() {
        assertEquals(releaseNotes.first().version, newestNewsVersion)
        for (notes in releaseNotes) {
            assertTrue("${notes.version} must say what changed", notes.changes.isNotBlank())
        }
    }

}
