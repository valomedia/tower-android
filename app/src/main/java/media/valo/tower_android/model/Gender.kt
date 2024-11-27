/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

//
//  Gender.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * An `enum` representing the gender of a person as either male, female, or other.
 */
enum class Gender {
    MALE {
        override fun toString(): String = "Männlich"
    },

    FEMALE {
        override fun toString(): String = "Weiblich"
    },

    OTHER {
        override fun toString(): String = "Divers"
    }
}
