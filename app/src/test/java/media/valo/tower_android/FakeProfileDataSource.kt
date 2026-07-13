/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

//
//  FakeProfileDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
 * This discards anything put into it and will emit static (but reasonable) values for all fields.
 */
class FakeProfileDataSource : ProfileDataSource {

    override val firstNameFlow: Flow<String?> = flow { emit(FIRST_NAME) }

    override suspend fun setFirstName(firstName: String?) = Unit

    override val lastNameFlow: Flow<String?> = flow { emit(LAST_NAME) }

    override suspend fun setLastName(lastName: String?) = Unit

    override val genderFlow: Flow<Gender?> = flow { emit(Gender.MALE) }

    override suspend fun setGender(gender: Gender?) = Unit

    override val birthdateFlow: Flow<LocalDate?> = flow { emit(null) }

    override suspend fun setBirthdate(birthdate: LocalDate?) = Unit

    override val phoneFlow: Flow<String?> = flow { emit(PHONE) }

    override suspend fun setPhone(phone: String?) = Unit

    override val emailFlow: Flow<String?> = flow { emit(EMAIL) }

    override suspend fun setEmail(email: String?) = Unit

}
