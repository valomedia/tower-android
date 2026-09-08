/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.elements

import de.tower_assist.tower_android.ui.routes.closed.ClosedScreen
import de.tower_assist.tower_android.ui.routes.loading.LoadingScreen
import de.tower_assist.tower_android.ui.routes.login.LoginScreen
import de.tower_assist.tower_android.ui.routes.outdated.OutdatedAppVersionScreen
import org.junit.Assert.assertTrue
import org.junit.Test

//
//  AppBarTest.kt
//  Tower_Android
//

/**
 * Tests for which destinations the app bar offers the drawer on.
 *
 * The drawer leads to the home screen, so a destination that shows it can be used to get past
 * whatever the user was being held at.
 */
class AppBarTest {

    @Test
    fun `offers no way out of the screens that gate the rest of the app`() {
        for (gate in listOf(
            LoginScreen::class,
            ClosedScreen::class,
            OutdatedAppVersionScreen::class,
            LoadingScreen::class
        )) {
            assertTrue("$gate must show no navigation icon", gate in destinationsWithoutNavigationIcon)
        }
    }

}
