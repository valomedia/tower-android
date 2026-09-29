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

package de.tower_assist.tower_android.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

//
//  UserProfile.kt
//  Tower_Android
//

/**
 * The profile information of the user, as provided to the assistant.
 *
 * @param firstName The first name of the user, if known.
 * @param lastName  The last name of the user, if known.
 * @param gender    The gender of the user, if known.
 * @param birthdate The birth date of the user, if known.
 * @param phone     The preferred phone number for contacting the user, if known.
 * @param email     The preferred email address for contacting the user, if known.
 */
@Serializable
data class UserProfile(
    val firstName: String?,
    val lastName: String?,
    val gender: Gender?,
    val birthdate: LocalDate?,
    val phone: String?,
    val email: String?
)
