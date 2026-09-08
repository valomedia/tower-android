/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//
//  Gender.kt
//  Tower_Android
//

/**
 * An `enum` representing the gender of a person as either male, female, or other.
 */
@Serializable
enum class Gender {

    @SerialName("M")
    MALE {
        override fun toString(): String = "Männlich"
    },

    @SerialName("F")
    FEMALE {
        override fun toString(): String = "Weiblich"
    },

    @SerialName("X")
    OTHER {
        override fun toString(): String = "Divers"
    }

}
