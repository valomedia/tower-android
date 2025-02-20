/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

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
//  Created by:
//      * Jean-Pierre Höhmann
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
