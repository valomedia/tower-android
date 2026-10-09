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

package media.valo.tower_android.data.local.preferences.profile

//
//  DummyProfileDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import media.valo.tower_android.model.Gender

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
