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

package media.valo.tower_android.ui.elements

import media.valo.tower_android.ui.routes.closed.ClosedScreen
import media.valo.tower_android.ui.routes.loading.LoadingScreen
import media.valo.tower_android.ui.routes.login.LoginScreen
import media.valo.tower_android.ui.routes.outdated.OutdatedAppVersionScreen
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
