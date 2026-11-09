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

package media.valo.tower_android.data.local.preferences.settings

//
//  SettingsDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow

/**
 * A data source for application-wide settings.
 */
interface SettingsDataSource {

    /**
     * A `Flow` that emits the api endpoint for the TOWER api every time it is updated.
     *
     * This will emit the new api endpoint to use for connections to the TOWER backend each time the
     * api endpoint is set. When the api endpoint is unset, it will emit `null`.
     */
    val apiEndpointFlow: Flow<String?>

    /**
     * Change the api endpoint for the TOWER api.
     *
     * This is used to update the api endpoint to use for connections to the TOWER backend (such as
     * when switching between the development and production environments).
     *
     * @param apiEndpoint   The new api endpoint to set, or `null` to unset the api endpoint.
     */
    suspend fun setApiEndpoint(apiEndpoint: String?)

    /**
     * A `Flow` that emits the version whose news the user has seen every time it is updated.
     *
     * This is the version the news screen was last shown for. It is recorded once a start has
     * passed every startup gate, and is what the news screen is suppressed on for later starts.
     * When it is unset, such as on a fresh install or after the app data has been cleared, this
     * will emit `null`.
     */
    val lastSeenNewsVersionFlow: Flow<String?>

    /**
     * Change the version whose news the user has seen.
     *
     * @param version   The version to record, or `null` to unset it.
     */
    suspend fun setLastSeenNewsVersion(version: String?)

    /**
     * A `Flow` that emits when the free trial started every time it is updated.
     *
     * The trial starts with the first call that reaches the assistant, and this records that moment
     * as milliseconds since the epoch. Until then, such as on a fresh install or for a user who has
     * not called since updating to a version with subscriptions, this will emit `null`.
     */
    val trialStartedAtFlow: Flow<Long?>

    /**
     * Change when the free trial started.
     *
     * @param trialStartedAt    The start as milliseconds since the epoch, or `null` to unset it.
     */
    suspend fun setTrialStartedAt(trialStartedAt: Long?)

    /**
     * A `Flow` that emits whether the user has been told about the free trial every time it is
     * updated.
     *
     * The notice goes out on the pricing screen after the first call, and this records that it has
     * been shown. It is kept apart from the start of the trial, because the first call can end out
     * of sight (in picture-in-picture, say), in which case the notice has to wait for the next
     * start. When it is unset, this will emit `null`, which counts as not seen.
     */
    val trialNoticeSeenFlow: Flow<Boolean?>

    /**
     * Change whether the user has been told about the free trial.
     *
     * @param seen  Whether the notice has been shown, or `null` to unset it.
     */
    suspend fun setTrialNoticeSeen(seen: Boolean?)

}
