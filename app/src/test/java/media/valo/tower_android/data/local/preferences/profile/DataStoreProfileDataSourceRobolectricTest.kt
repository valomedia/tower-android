/******************************************************************************
 * Copyright (c) 2026 valo.media GmbH                                         *
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

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import media.valo.tower_android.createTemporaryPreferencesDataStore
import media.valo.tower_android.model.Gender
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  DataStoreProfileDataSourceRobolectricTest.kt
//  Tower_Android
//

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class DataStoreProfileDataSourceRobolectricTest {

    private val dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun `persists profile fields through android data store`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataStore = createTemporaryPreferencesDataStore(
            context = context,
            scope = dataStoreScope,
            name = "robolectric_profile"
        )
        val dataSource = DataStoreProfileDataSource(dataStore)
        val birthdate = LocalDate(1989, 7, 14)

        assertNull(dataSource.firstNameFlow.first())
        assertNull(dataSource.lastNameFlow.first())
        assertNull(dataSource.genderFlow.first())
        assertNull(dataSource.birthdateFlow.first())
        assertNull(dataSource.phoneFlow.first())
        assertNull(dataSource.emailFlow.first())

        dataSource.setFirstName("Thea")
        dataSource.setLastName("Test")
        dataSource.setGender(Gender.FEMALE)
        dataSource.setBirthdate(birthdate)
        dataSource.setPhone("+49 152 28817386")
        dataSource.setEmail("thea.test@example.com")

        assertEquals("Thea", dataSource.firstNameFlow.first())
        assertEquals("Test", dataSource.lastNameFlow.first())
        assertEquals(Gender.FEMALE, dataSource.genderFlow.first())
        assertEquals(birthdate, dataSource.birthdateFlow.first())
        assertEquals("+49 152 28817386", dataSource.phoneFlow.first())
        assertEquals("thea.test@example.com", dataSource.emailFlow.first())
    }

    @Test
    fun `clears profile fields from android data store`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataStore = createTemporaryPreferencesDataStore(
            context = context,
            scope = dataStoreScope,
            name = "robolectric_profile_clear"
        )
        val dataSource = DataStoreProfileDataSource(dataStore)

        dataSource.setFirstName("Thea")
        dataSource.setLastName("Test")
        dataSource.setGender(Gender.OTHER)
        dataSource.setBirthdate(LocalDate(1989, 7, 14))
        dataSource.setPhone("+49 152 28817386")
        dataSource.setEmail("thea.test@example.com")

        dataSource.setFirstName(null)
        dataSource.setLastName(null)
        dataSource.setGender(null)
        dataSource.setBirthdate(null)
        dataSource.setPhone(null)
        dataSource.setEmail(null)

        assertNull(dataSource.firstNameFlow.first())
        assertNull(dataSource.lastNameFlow.first())
        assertNull(dataSource.genderFlow.first())
        assertNull(dataSource.birthdateFlow.first())
        assertNull(dataSource.phoneFlow.first())
        assertNull(dataSource.emailFlow.first())
    }

}
