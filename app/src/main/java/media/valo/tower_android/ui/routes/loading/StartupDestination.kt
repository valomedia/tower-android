/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.loading

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
