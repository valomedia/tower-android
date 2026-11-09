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

package media.valo.tower_android.model

//
//  SubscriptionStatus.kt
//  Tower_Android
//

/**
 * How many days the free trial lasts, as told to the user.
 *
 * This only feeds the notice on the pricing screen. Nothing tracks the trial's end yet: until
 * purchasing lands, a trial that has started is a trial that runs on, so `SubscriptionStatus`
 * never leaves `TRIAL` on its own.
 */
const val TRIAL_DURATION_DAYS = 7

/**
 * Where the user stands with regard to a subscription.
 *
 * Purchasing is not implemented yet, so for now a user is either not on anything, or on the free
 * trial that starts with their first call. The trial has no end yet either, so this only ever
 * moves from `NONE` to `TRIAL`.
 *
 * @param label The German label the menu shows next to the subscription item, or `null` when
 *              there is nothing to say. Unlike the other enums in this package this is not
 *              `toString()`, so that `NONE` does not print as an empty string in logs and test
 *              failures.
 */
enum class SubscriptionStatus(val label: String?) {

    /**
     * The user has not made a call yet, so nothing has started for them.
     *
     * There is no label, because there is nothing to say before the first call.
     */
    NONE(label = null),

    /**
     * The user has made their first call, which started the free trial.
     *
     * Until purchasing lands, the trial's end is not tracked, so this says that the trial has
     * started, not that it is still running.
     */
    TRIAL(label = "Testphase")

}
