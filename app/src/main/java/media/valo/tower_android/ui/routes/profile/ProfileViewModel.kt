/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.profile

//
//  ProfileViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.model.Gender
import java.time.LocalDate
import javax.inject.Inject

/**
 * `ViewModel` for `ProfileScreen`.
 *
 * @param profileRepository `ProfileRepository` dependency.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    /**
     * Get the first name (if any).
     *
     * @return The first name that is currently set, or `null` if the first name is unset.
     */
    suspend fun getFirstName(): String? = profileRepository.getFirstName()

    /**
     * Change the first name.
     *
     * @param firstName The new name to set, or `null` to unset the name.
     */
    suspend fun setFirstName(firstName: String?) = profileRepository.setFirstName(firstName)

    /**
     * Get the last name (if any).
     *
     * @return The last name that is currently set, or `null` if the last name is unset.
     */
    suspend fun getLastName(): String? = profileRepository.getLastName()

    /**
     * Change the last name.
     *
     * @param lastName  The new name to set, or `null` to unset the name.
     */
    suspend fun setLastName(lastName: String?) = profileRepository.setLastName(lastName)

    /**
     * Get the `Gender` (if any).
     *
     * @return The `Gender` that is currently set, or `null` if the `Gender` is unset.
     */
    suspend fun getGender(): Gender? = profileRepository.getGender()

    /**
     * Change the `Gender`.
     *
     * @param gender    The new `Gender` to set, or `null` to unset the `Gender`.
     */
    suspend fun setGender(gender: Gender?) = profileRepository.setGender(gender)

    /**
     * Get the birthdate (if any).
     *
     * @return The birthdate that is currently set, or `null` if the birthdate is unset.
     */
    suspend fun getBirthdate(): LocalDate? = profileRepository.getBirthdate()

    /**
     * Change the birthdate.
     *
     * @param birthdate The new birthdate to set, or `null` to unset the birthdate.
     */
    suspend fun setBirthdate(birthdate: LocalDate?) = profileRepository.setBirthdate(birthdate)

    /**
     * Get the phone number (if any).
     *
     * @return The number that is currently set, or `null` if the phone number is unset.
     */
    suspend fun getPhone(): String? = profileRepository.getPhone()

    /**
     * Change the phone number.
     *
     * @param phone The new number to set, or `null` to unset the phone number.
     */
    suspend fun setPhone(phone: String?) = profileRepository.setPhone(phone)

    /**
     * Get the e-mail address (if any).
     *
     * @return The e-mail address that is currently set, or `null` if the e-mail address is unset.
     */
    suspend fun getEmail(): String? = profileRepository.getEmail()

    /**
     * Change the e-mail address.
     *
     * @param email The new e-mail address to set, or `null` to unset the e-mail address.
     */
    suspend fun setEmail(email: String?) = profileRepository.setEmail(email)

}
