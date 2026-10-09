/******************************************************************************
 * Copyright (c) 2024-2026 valo.media GmbH                                    *
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
