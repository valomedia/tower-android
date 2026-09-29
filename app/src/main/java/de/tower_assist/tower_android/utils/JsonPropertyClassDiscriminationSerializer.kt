/******************************************************************************
 * Copyright (c) 2025-2026 valo.media GmbH                                    *
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

package de.tower_assist.tower_android.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

//
//  JsonPropertyClassDiscriminationSerializer.kt
//  Tower_Android
//

/**
 * Modify a polymorphic JSON serializer to use an object property as a class discriminator.
 *
 * This will take a serializer that serializes an object with a type-property as a class
 * discriminator, and return a serializer that instead wraps the object in another object with
 * exactly one property, whose name acts as a class discriminator.
 *
 * @param tSerializer The serializer to apply the transformation to.
 */
open class JsonPropertyClassDiscriminationSerializer<T: Any>(
    tSerializer: KSerializer<T>
): JsonTransformingSerializer<T>(tSerializer) {

    override fun transformSerialize(element: JsonElement): JsonElement {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
        require(type != null) { "Type property must be present to serialize the object" }
        return JsonObject(mapOf(type to JsonObject(element.jsonObject.filterKeys { it != "type" })))
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        require(element.jsonObject.keys.size == 1) { "Object must have exactly one property" }
        return JsonObject(
            element.jsonObject.values.first().jsonObject
                + mapOf("type" to JsonPrimitive(element.jsonObject.keys.first())))
    }

}
