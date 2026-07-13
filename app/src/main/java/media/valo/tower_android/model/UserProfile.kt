/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.model

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
