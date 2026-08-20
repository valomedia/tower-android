/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.login

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import media.valo.tower_android.FakeProfileDataSource
import media.valo.tower_android.FakeSettingsDataSource
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.data.remote.newsletter.DummyNewsletterDataSource
import media.valo.tower_android.data.remote.newsletter.NewsletterRepository
import media.valo.tower_android.ui.routes.news.newestNewsVersion
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
