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
//  DataStoreProfileDataSource.kt
//  Tower_Android
//

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import media.valo.tower_android.model.Gender
import media.valo.tower_android.utils.ProfileDataStore
import media.valo.tower_android.utils.get
import media.valo.tower_android.utils.set
import javax.inject.Inject

/**
 * A `ProfileDataSource` backed by a `DataStore`.
 *
 * @param dataStore `DataStore` dependency.
 */
class DataStoreProfileDataSource @Inject constructor(
    @ProfileDataStore private val dataStore: DataStore<Preferences>
) : ProfileDataSource {

    private val firstNameKey: Preferences.Key<String> = stringPreferencesKey("first_name")

    override val firstNameFlow: Flow<String?> = dataStore.get(firstNameKey)

    override suspend fun setFirstName(firstName: String?) = dataStore.set(firstNameKey, firstName)

    private val lastNameKey: Preferences.Key<String> = stringPreferencesKey("last_name")

    override val lastNameFlow: Flow<String?> = dataStore.get(lastNameKey)

    override suspend fun setLastName(lastName: String?) = dataStore.set(lastNameKey, lastName)

    private val genderKey: Preferences.Key<String> = stringPreferencesKey("gender")

    override val genderFlow: Flow<Gender?> = dataStore.get(genderKey).map { value ->
        Gender.entries.firstOrNull { gender -> gender.name == value }
    }

    override suspend fun setGender(gender: Gender?) = dataStore.set(genderKey, gender?.name)

    private val birthdateKey: Preferences.Key<String> = stringPreferencesKey("birthdate")

    override val birthdateFlow: Flow<LocalDate?> =
        dataStore.get(birthdateKey).map { dateString ->
            dateString?.let { LocalDate.parse(it) }
        }

    override suspend fun setBirthdate(birthdate: LocalDate?) =
        dataStore.set(birthdateKey, birthdate?.toString())

    private val phoneKey: Preferences.Key<String> = stringPreferencesKey("phone")

    override val phoneFlow: Flow<String?> = dataStore.get(phoneKey)

    override suspend fun setPhone(phone: String?) = dataStore.set(phoneKey, phone)

    private val emailKey: Preferences.Key<String> = stringPreferencesKey("email")

    override val emailFlow: Flow<String?> = dataStore.get(emailKey)

    override suspend fun setEmail(email: String?) = dataStore.set(emailKey, email)

}
