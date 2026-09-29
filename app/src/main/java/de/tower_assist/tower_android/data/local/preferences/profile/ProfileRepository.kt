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

package de.tower_assist.tower_android.data.local.preferences.profile

//
//  ProfileRepository.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDate
import de.tower_assist.tower_android.model.Gender
import de.tower_assist.tower_android.model.UserProfile
import javax.inject.Inject

/**
 * Repository for information about the user.
 *
 * @param profileDataSource `ProfileDataSource` dependency.
 */
class ProfileRepository @Inject constructor(
    private val profileDataSource: ProfileDataSource
) {

    /**
     * A `Flow` that emits the first name every time it is updated.
     *
     * This will emit the new name each time it is set. When the name is unset, it will emit `null`.
     */
    val firstNameFlow: Flow<String?> = profileDataSource.firstNameFlow

    /**
     * Get the first name (if any).
     *
     * @return The first name that is currently set, or `null` if the first name is unset.
     */
    suspend fun getFirstName(): String? = firstNameFlow.firstOrNull()

    /**
     * Change the first name.
     *
     * @param firstName The new name to set, or `null` to unset the name.
     */
    suspend fun setFirstName(firstName: String?) = profileDataSource.setFirstName(firstName)

    /**
     * A `Flow` that emits the last name every time it is updated.
     *
     * This will emit the new name each time it is set. When the name is unset, it will emit `null`.
     */
    val lastNameFlow: Flow<String?> = profileDataSource.lastNameFlow

    /**
     * Get the last name (if any).
     *
     * @return The last name that is currently set, or `null` if the last name is unset.
     */
    suspend fun getLastName(): String? = lastNameFlow.firstOrNull()

    /**
     * Change the last name.
     *
     * @param lastName  The new name to set, or `null` to unset the name.
     */
    suspend fun setLastName(lastName: String?) = profileDataSource.setLastName(lastName)

    /**
     * A `Flow` that emits the `Gender` every time it is updated.
     *
     * This will emit the new `Gender` each time it is set. When the gender is unset, it will emit
     * `null`.
     */
    val genderFlow: Flow<Gender?> = profileDataSource.genderFlow

    /**
     * Get the `Gender` (if any).
     *
     * @return The `Gender` that is currently set, or `null` if the `Gender` is unset.
     */
    suspend fun getGender(): Gender? = genderFlow.firstOrNull()

    /**
     * Change the `Gender`.
     *
     * @param gender    The new `Gender` to set, or `null` to unset the `Gender`.
     */
    suspend fun setGender(gender: Gender?) = profileDataSource.setGender(gender)

    /**
     * A `Flow` that emits the birthdate every time it is updated.
     *
     * This will emit the new birthdate each time it is set. When the birthdate is unset, it will
     * emit `null`.
     */
    val birthdateFlow: Flow<LocalDate?> = profileDataSource.birthdateFlow

    /**
     * Get the birthdate (if any).
     *
     * @return The birthdate that is currently set, or `null` if the birthdate is unset.
     */
    suspend fun getBirthdate(): LocalDate? = birthdateFlow.firstOrNull()

    /**
     * Change the birth date.
     *
     * @param birthdate The new birthdate to set, or `null` to unset the birthdate.
     */
    suspend fun setBirthdate(birthdate: LocalDate?) = profileDataSource.setBirthdate(birthdate)

    /**
     * A `Flow` that emits the phone number every time it is updated.
     *
     * This will emit the new phone number each time it is set. When the phone number is unset, it
     * will emit `null`.
     */
    val phoneFlow: Flow<String?> = profileDataSource.phoneFlow

    /**
     * Get the phone number (if any).
     *
     * @return The phone number that is currently set, or `null` if the phone number is unset.
     */
    suspend fun getPhone(): String? = phoneFlow.firstOrNull()

    /**
     * Change the phone number.
     *
     * @param phone The new phone number to set, or `null` to unset the phone number.
     */
    suspend fun setPhone(phone: String?) = profileDataSource.setPhone(phone)

    /**
     * A `Flow` that emits the e-mail address every time it is updated.
     *
     * This will emit the new e-mail address each time it is set. When the e-mail address is unset,
     * it will emit `null`.
     */
    val emailFlow: Flow<String?> = profileDataSource.emailFlow

    /**
     * Get the e-mail address (if any).
     *
     * @return The e-mail address that is currently set, or `null` if the e-mail address is unset.
     */
    suspend fun getEmail(): String? = emailFlow.firstOrNull()

    /**
     * Change the e-mail address.
     *
     * @param email The new e-mail address to set, or `null` to unset the e-mail address.
     */
    suspend fun setEmail(email: String?) = profileDataSource.setEmail(email)

    /**
     * Get the full UserProfile.
     *
     * @return The contents of the ProfileRepository as a UserProfile.
     */
    suspend fun getUserProfile(): UserProfile = UserProfile(
        firstName = getFirstName(),
        lastName = getLastName(),
        gender = getGender(),
        birthdate = getBirthdate(),
        phone = getPhone(),
        email = getEmail()
    )

    /**
     * Write a UserProfile to the repository, overwriting all fields.
     *
     * @param userProfile The UserProfile to store, or `null` to clear the user profile.
     */
    suspend fun setUserProfile(userProfile: UserProfile?) {
        setFirstName(userProfile?.firstName)
        setLastName(userProfile?.lastName)
        setGender(userProfile?.gender)
        setBirthdate(userProfile?.birthdate)
        setPhone(userProfile?.phone)
        setEmail(userProfile?.email)
    }

    /**
     * Check if the user has provided all profile information required to make a call.
     *
     * @return Whether the user is allowed to make a call based on the profile information provided.
     */
    suspend fun hasProfile(): Boolean = !getFirstName().isNullOrBlank()

}
