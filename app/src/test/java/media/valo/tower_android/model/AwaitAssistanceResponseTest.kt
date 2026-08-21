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
//  AwaitAssistanceResponseTest.kt
//  Tower_Android
//
//  Created by:
//      * Yatsar (Agent)
//

class AwaitAssistanceResponseTest {

    @Test
    fun `decodes queue position from await assistance response`() {
        val response = Json.decodeFromString<AwaitAssistanceResponse>("""{"position":2}""")

        assertEquals(2, response.position)
    }

    @Test
    fun `encodes queue position in await assistance response`() {
        val encoded = Json.encodeToString(AwaitAssistanceResponse(position = 2))

        assertEquals(
            JsonObject(mapOf("position" to JsonPrimitive(2))),
            Json.parseToJsonElement(encoded)
        )
    }

}
