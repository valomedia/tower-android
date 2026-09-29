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

package de.tower_assist.tower_android.ui.routes.loading

//
//  StartupDestination.kt
//  Tower_Android
//

/**
 * The screen to show once the startup checks have completed.
 */
sealed interface StartupDestination {

    /**
     * The app is too old to talk to the backend.
     */
    data object Outdated : StartupDestination

    /**
     * The backend could not be reached, or the user still has to provide a profile.
     */
    data object Login : StartupDestination

    /**
     * The assistants are not currently taking calls.
     *
     * @param schedule  The opening hours to show to the user.
     */
    data class Closed(val schedule: String) : StartupDestination

    /**
     * The app has been updated since it was last started, so the news are shown.
     */
    data object News : StartupDestination

    /**
     * Everything is in order and the user can place a call.
     */
    data object Home : StartupDestination

}
