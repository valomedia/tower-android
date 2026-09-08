/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.data.local.preferences.profile

//
//  DummyProfileDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import de.tower_assist.tower_android.model.Gender

/**
 * A dummy implementation of `ProfileDataSource`.
 *
 * This discards anything put into it and will always emit `null` for all fields.
 */
class DummyProfileDataSource : ProfileDataSource {
    override val firstNameFlow: Flow<String?> = flow { emit(null) }

    override suspend fun setFirstName(firstName: String?) = Unit

    override val lastNameFlow: Flow<String?> = flow { emit(null) }

    override suspend fun setLastName(lastName: String?) = Unit

    override val genderFlow: Flow<Gender?> = flow { emit(null) }

    override suspend fun setGender(gender: Gender?) = Unit

    override val birthdateFlow: Flow<LocalDate?> = flow { emit(null) }

    override suspend fun setBirthdate(birthdate: LocalDate?) = Unit

    override val phoneFlow: Flow<String?> = flow { emit(null) }

    override suspend fun setPhone(phone: String?) = Unit

    override val emailFlow: Flow<String?> = flow { emit(null) }

    override suspend fun setEmail(email: String?) = Unit
}
