/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.data.local.preferences.profile

//
//  ProfileDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import de.tower_assist.tower_android.model.Gender

/**
 * A data source for information about the user.
 */
interface ProfileDataSource {

    /**
     * A `Flow` that emits the first name every time it is updated.
     *
     * This will emit the new name each time it is set. When the name is unset, it will emit `null`.
     */
    val firstNameFlow: Flow<String?>

    /**
     * Change the first name.
     *
     * @param firstName The new name to set, or `null` to unset the name.
     */
    suspend fun setFirstName(firstName: String?)

    /**
     * A `Flow` that emits the last name every time it is updated.
     *
     * This will emit the new name each time it is set. When the name is unset, it will emit `null`.
     */
    val lastNameFlow: Flow<String?>

    /**
     * Change the last name.
     *
     * @param lastName  The new name to set, or `null` to unset the name.
     */
    suspend fun setLastName(lastName: String?)

    /**
     * A `Flow` that emits the `Gender` every time it is updated.
     *
     * This will emit the new `Gender` each time it is set. When the `Gender` is unset, it will emit
     * `null`.
     */
    val genderFlow: Flow<Gender?>

    /**
     * Change the `Gender`.
     *
     * @param gender    The new `Gender` to set, or `null` to unset the `Gender`.
     */
    suspend fun setGender(gender: Gender?)

    /**
     * A `Flow` that emits the birthdate every time it is updated.
     *
     * This will emit the new birthdate each time it is set. When the birthdate is unset, it will
     * emit `null`.
     */
    val birthdateFlow: Flow<LocalDate?>

    /**
     * Change the birth date.
     *
     * @param birthdate The new birthdate to set, or `null` to unset the birthdate.
     */
    suspend fun setBirthdate(birthdate: LocalDate?)

    /**
     * A `Flow` that emits the phone number every time it is updated.
     *
     * This will emit the new phone number each time it is set. When the phone number is unset, it
     * will emit `null`.
     */
    val phoneFlow: Flow<String?>

    /**
     * Change the phone number.
     *
     * @param phone The new phone number to set, or `null` to unset the phone number.
     */
    suspend fun setPhone(phone: String?)

    /**
     * A `Flow` that emits the e-mail address every time it is updated.
     *
     * This will emit the new e-mail address each time it is set. When the e-mail address is unset,
     * it will emit `null`.
     */
    val emailFlow: Flow<String?>

    /**
     * Change the e-mail address.
     *
     * @param email The new e-mail address to set, or `null` to unset the e-mail address.
     */
    suspend fun setEmail(email: String?)

}
