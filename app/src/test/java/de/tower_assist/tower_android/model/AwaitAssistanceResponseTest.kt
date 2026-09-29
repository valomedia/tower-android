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

package de.tower_assist.tower_android.model

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
