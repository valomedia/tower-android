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

package de.tower_assist.tower_android

//
//  FakeProfileDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate
import de.tower_assist.tower_android.data.local.preferences.profile.ProfileDataSource
import de.tower_assist.tower_android.model.Gender

private const val FIRST_NAME = "Theo"
private const val LAST_NAME = "Test"
private const val PHONE = "+49 152 28817386"
private const val EMAIL = "theo.test@example.com"

/**
 * A fake implementation of `ProfileDataSource`.
 *
 * This starts out with static (but reasonable) values and keeps anything put into it in memory.
 * Use `DummyProfileDataSource` where writes should be discarded instead.
 */
class FakeProfileDataSource : ProfileDataSource {

    private val firstName = MutableStateFlow<String?>(FIRST_NAME)

    override val firstNameFlow: Flow<String?> = firstName

    override suspend fun setFirstName(firstName: String?) {
        this.firstName.value = firstName
    }

    private val lastName = MutableStateFlow<String?>(LAST_NAME)

    override val lastNameFlow: Flow<String?> = lastName

    override suspend fun setLastName(lastName: String?) {
        this.lastName.value = lastName
    }

    private val gender = MutableStateFlow<Gender?>(Gender.MALE)

    override val genderFlow: Flow<Gender?> = gender

    override suspend fun setGender(gender: Gender?) {
        this.gender.value = gender
    }

    private val birthdate = MutableStateFlow<LocalDate?>(null)

    override val birthdateFlow: Flow<LocalDate?> = birthdate

    override suspend fun setBirthdate(birthdate: LocalDate?) {
        this.birthdate.value = birthdate
    }

    private val phone = MutableStateFlow<String?>(PHONE)

    override val phoneFlow: Flow<String?> = phone

    override suspend fun setPhone(phone: String?) {
        this.phone.value = phone
    }

    private val email = MutableStateFlow<String?>(EMAIL)

    override val emailFlow: Flow<String?> = email

    override suspend fun setEmail(email: String?) {
        this.email.value = email
    }

}
