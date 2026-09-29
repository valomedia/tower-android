/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
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
