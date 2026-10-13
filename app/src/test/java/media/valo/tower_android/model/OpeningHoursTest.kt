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

package media.valo.tower_android.model

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

//
//  OpeningHoursTest.kt
//  Tower_Android
//

class OpeningHoursTest {

    @Test
    fun `decodes opening hours with API status names`() {
        val openingHours = Json.decodeFromString<OpeningHours>(
            """{"status":"open","description":"Heute geöffnet."}"""
        )

        assertEquals(Status.OPEN, openingHours.status)
        assertEquals("Heute geöffnet.", openingHours.description)
    }

    @Test
    fun `encodes opening hours with API status names`() {
        val encoded = Json.encodeToString(
            OpeningHours(
                status = Status.CLOSED,
                description = "Heute geschlossen."
            )
        )

        assertEquals(
            JsonObject(
                mapOf(
                    "status" to JsonPrimitive("closed"),
                    "description" to JsonPrimitive("Heute geschlossen.")
                )
            ),
            Json.parseToJsonElement(encoded)
        )
    }

}
