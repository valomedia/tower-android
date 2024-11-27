/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.profile

//
//  DummyProfileDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import media.valo.tower_android.model.Gender
import java.time.LocalDate

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
