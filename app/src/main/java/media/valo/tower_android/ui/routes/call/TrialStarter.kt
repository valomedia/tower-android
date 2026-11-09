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

package media.valo.tower_android.ui.routes.call

//
//  TrialStarter.kt
//  Tower_Android
//

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.utils.AppScope
import java.io.IOException
import javax.inject.Inject

/**
 * How long to wait for the settings on the way out of a call, in milliseconds.
 *
 * This covers both the start of the free trial being recorded and the look-up of whether the
 * notice about it is owed. Either normally takes a few milliseconds. The bound only keeps a
 * settings store that has stopped answering from holding the user on the call screen.
 */
private const val SETTINGS_TIMEOUT_MILLIS = 1000L

/**
 * Tag added to log messages related to the TrialStarter.
 */
private const val TAG = "TrialStarter"

/**
 * Starts the free trial with the user's first call and tells whether the notice about it is owed.
 *
 * The trial starts with the first call that reaches the assistant, and only with that one. This is
 * kept apart from `CallViewModel` so that the rule can be exercised without standing up the call
 * stack, which the `ViewModel` cannot be separated from.
 *
 * @param settingsRepository    `SettingsRepository` dependency.
 * @param appScope              Scope for the write, which has to outlive the call screen.
 */
class TrialStarter @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @AppScope private val appScope: CoroutineScope
) {

    private var trialStartJob: Job? = null

    /**
     * Record that a call has reached the assistant, which starts the free trial unless it has
     * started already.
     *
     * The start is recorded in the background, so that the call goes on regardless of how the
     * settings are doing. Losing the start is not worth losing the call over, so a settings store
     * that cannot be written is only logged, and the next call that connects gets another go.
     */
    fun onCallConnected() {
        trialStartJob = appScope.launch {
            try {
                settingsRepository.startTrialIfNeeded(now = System.currentTimeMillis())
            } catch (e: IOException) {
                Log.w(TAG, "Could not record the start of the free trial", e)
            }
        }
    }

    /**
     * Whether the user has yet to be told that their first call started the free trial.
     *
     * The notice is owed from the start of the trial until the pricing screen has shown it. The
     * start is recorded in the background once the call connects, so this waits for that to finish
     * first, in case the call dropped right after connecting.
     *
     * This is asked on the way out of the call, where settings trouble must not keep the user from
     * leaving: the wait for the start and the look-up after it are bounded together, and the
     * settings being unreadable counts as no notice owed. Both are only logged, because the next
     * start asks again, and a trial start that is recorded late is still recorded.
     *
     * @return Whether the notice is still owed to the user.
     */
    suspend fun isTrialNoticePending(): Boolean {
        val isPending = withTimeoutOrNull(SETTINGS_TIMEOUT_MILLIS) {
            trialStartJob?.join()
            try {
                settingsRepository.isTrialNoticePending()
            } catch (e: IOException) {
                Log.w(TAG, "Could not look up whether the trial notice is owed", e)
                false
            }
        }
        if (isPending == null) {
            Log.w(TAG, "Gave up waiting for the settings on the way out of the call")
        }
        return isPending ?: false
    }

}
