/******************************************************************************
 * Copyright (c) 2026 valo.media GmbH                                         *
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

package media.valo.tower_android.ui.routes.news

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
