/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.news

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  NewsScreenRobolectricTest.kt
//  Tower_Android
//

/**
 * Release notes spanning enough versions and text to need scrolling.
 */
private val manyReleaseNotes = listOf(
    ReleaseNotes(version = "3.0.0", changes = "Die neueste Änderung."),
    ReleaseNotes(version = "2.0.0", changes = "Die mittlere Änderung."),
    ReleaseNotes(
        version = "1.0.0",
        changes = List(40) { "Ein alter Satz über eine alte Änderung." }.joinToString(" ")
                + " Die älteste Änderung."
    )
)

/**
 * Tests for what the news screen puts on the screen.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class NewsScreenRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Show the news screen for a set of release notes.
     *
     * @param notes The release notes to show.
     */
    private fun showNews(notes: List<ReleaseNotes>) {
        composeTestRule.setContent {
            NewsScreen(notes = notes, modifier = Modifier.fillMaxSize())
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `shows every version in the changelog and not just the newest`() {
        showNews(manyReleaseNotes)

        for (notes in manyReleaseNotes) {
            composeTestRule.onNodeWithText(notes.version).performScrollTo().assertIsDisplayed()
        }
    }

    @Test
    fun `scrolls down to the notes of the oldest version`() {
        showNews(manyReleaseNotes)

        composeTestRule
            .onNodeWithText("Die älteste Änderung.", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun `renders the markup in the notes rather than showing it`() {
        showNews(listOf(ReleaseNotes(version = "1.0.0", changes = "<b>Wichtig</b>: Ein Hinweis.")))

        composeTestRule.onNodeWithText("Wichtig: Ein Hinweis.").assertIsDisplayed()
    }

    @Test
    fun `shows the release notes the app ships with by default`() {
        composeTestRule.setContent { NewsScreen(modifier = Modifier.fillMaxSize()) }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(newestNewsVersion).performScrollTo().assertIsDisplayed()
    }

}
