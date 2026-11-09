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

package media.valo.tower_android.data.local.preferences.settings

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import media.valo.tower_android.FakeSettingsDataSource
import media.valo.tower_android.model.SubscriptionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

//
//  SettingsRepositoryTest.kt
//  Tower_Android
//

private const val TRIAL_STARTED_AT = 1_700_000_000_000L
private const val ONE_DAY_LATER = TRIAL_STARTED_AT + 24 * 60 * 60 * 1_000L

/**
 * Tests for how the settings repository starts the trial and derives the subscription status and
 * the trial notice from it.
 */
class SettingsRepositoryTest {

    private val repository = SettingsRepository(FakeSettingsDataSource())

    @Test
    fun `puts the user on nothing before the trial has started`() = runBlocking {
        assertNull(repository.getTrialStartedAt())
        assertEquals(SubscriptionStatus.NONE, repository.subscriptionStatusFlow.first())
    }

    @Test
    fun `starts the trial with the first call`() = runBlocking {
        repository.startTrialIfNeeded(now = TRIAL_STARTED_AT)

        assertEquals(TRIAL_STARTED_AT, repository.getTrialStartedAt())
    }

    @Test
    fun `does not restart the trial with a later call`() = runBlocking {
        repository.startTrialIfNeeded(now = TRIAL_STARTED_AT)
        repository.startTrialIfNeeded(now = ONE_DAY_LATER)

        assertEquals(TRIAL_STARTED_AT, repository.getTrialStartedAt())
    }

    @Test
    fun `puts the user on the trial once it has started`() = runBlocking {
        repository.startTrialIfNeeded(now = TRIAL_STARTED_AT)

        assertEquals(SubscriptionStatus.TRIAL, repository.subscriptionStatusFlow.first())
    }

    @Test
    fun `owes no trial notice before the trial has started`() = runBlocking {
        assertFalse(repository.isTrialNoticePending())
    }

    @Test
    fun `owes the trial notice from the start of the trial until it has been seen`() = runBlocking {
        repository.startTrialIfNeeded(now = TRIAL_STARTED_AT)
        assertTrue(repository.isTrialNoticePending())

        repository.markTrialNoticeSeen()
        assertFalse(repository.isTrialNoticePending())
    }

    @Test
    fun `keeps the trial notice seen across later calls`() = runBlocking {
        repository.startTrialIfNeeded(now = TRIAL_STARTED_AT)
        repository.markTrialNoticeSeen()

        repository.startTrialIfNeeded(now = ONE_DAY_LATER)

        assertFalse(repository.isTrialNoticePending())
    }

}
