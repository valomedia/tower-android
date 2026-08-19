/******************************************************************************
 * Copyright (c) 2024-2026.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

//
//  FakeProfileDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate
import media.valo.tower_android.data.local.preferences.profile.ProfileDataSource
import media.valo.tower_android.model.Gender

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
