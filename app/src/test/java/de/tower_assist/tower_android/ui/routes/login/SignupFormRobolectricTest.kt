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

package de.tower_assist.tower_android.ui.routes.login

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import de.tower_assist.tower_android.FakeProfileDataSource
import de.tower_assist.tower_android.FakeSettingsDataSource
import de.tower_assist.tower_android.data.local.preferences.profile.ProfileRepository
import de.tower_assist.tower_android.data.local.preferences.settings.SettingsRepository
import de.tower_assist.tower_android.data.remote.newsletter.DummyNewsletterDataSource
import de.tower_assist.tower_android.data.remote.newsletter.NewsletterRepository
import de.tower_assist.tower_android.ui.routes.news.newestNewsVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  SignupFormRobolectricTest.kt
//  Tower_Android
//

/**
 * Tests for the signup form taking a first-time user off the news screen.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class SignupFormRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val settingsRepository = SettingsRepository(FakeSettingsDataSource())

    private val viewModel = SignupFormViewModel(
        profileRepository = ProfileRepository(FakeProfileDataSource()),
        newsletterRepository = NewsletterRepository(DummyNewsletterDataSource()),
        settingsRepository = settingsRepository,
        appScope = CoroutineScope(SupervisorJob())
    )

    @Test
    fun `marks the news as seen once the form is on screen`() {
        assertNull(runBlocking { settingsRepository.getLastSeenNewsVersion() })

        composeTestRule.setContent {
            SignupForm(navController = rememberNavController(), viewModel = viewModel)
        }
        composeTestRule.waitForIdle()

        assertEquals(
            newestNewsVersion,
            runBlocking { settingsRepository.getLastSeenNewsVersion() }
        )
    }

}
