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

import android.app.Application
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import media.valo.tower_android.FakeSettingsDataSource
import media.valo.tower_android.data.local.preferences.settings.SettingsDataSource
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

//
//  TrialStarterRobolectricTest.kt
//  Tower_Android
//

/**
 * Tests for how a call that reaches the assistant starts the free trial.
 *
 * These need Robolectric only because the paths that give up on the settings log a warning.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class TrialStarterRobolectricTest {

    private val settingsDataSource = TrialSettingsDataSource()

    private val settingsRepository = SettingsRepository(settingsDataSource)

    /**
     * Stands in for the app scope, which the write to the settings runs on.
     */
    private val appScope = CoroutineScope(Dispatchers.Default)

    private val trialStarter = TrialStarter(
        settingsRepository = settingsRepository,
        appScope = appScope
    )

    @After
    fun stopAppScope() {
        appScope.cancel()
    }

    @Test
    fun `owes no notice before any call has connected`() = runBlocking {
        assertFalse(trialStarter.isTrialNoticePending())
        assertNull(settingsRepository.getTrialStartedAt())
    }

    @Test
    fun `starts the trial when the call connects`() = runBlocking {
        trialStarter.onCallConnected()

        assertTrue(trialStarter.isTrialNoticePending())
        assertNotNull(settingsRepository.getTrialStartedAt())
    }

    @Test
    fun `leaves the start alone when a later call connects`() = runBlocking {
        trialStarter.onCallConnected()
        trialStarter.isTrialNoticePending()
        val trialStartedAt = settingsRepository.getTrialStartedAt()

        trialStarter.onCallConnected()
        trialStarter.isTrialNoticePending()

        assertEquals(trialStartedAt, settingsRepository.getTrialStartedAt())
        assertEquals(1, settingsDataSource.trialStartWrites)
    }

    @Test
    fun `counts a call that drops right after connecting`() = runBlocking {
        settingsDataSource.holdTrialStartWrites()
        trialStarter.onCallConnected()

        // The call is over before the start has been recorded, so the answer has to wait for it.
        val isPending = async { trialStarter.isTrialNoticePending() }
        yield()
        assertFalse(isPending.isCompleted)

        settingsDataSource.releaseTrialStartWrites()
        assertTrue(isPending.await())
    }

    @Test
    fun `does not hold the user on the call screen when the settings stop answering`() =
        runBlocking {
            settingsDataSource.holdTrialStartWrites()
            trialStarter.onCallConnected()

            assertFalse(trialStarter.isTrialNoticePending())
        }

    @Test
    fun `survives the start not being writable`() = runBlocking {
        settingsDataSource.trialStartWriteFailure = IOException("disk full")
        trialStarter.onCallConnected()

        assertFalse(trialStarter.isTrialNoticePending())
        assertNull(settingsRepository.getTrialStartedAt())
    }

    @Test
    fun `survives the settings not being readable`() = runBlocking {
        trialStarter.onCallConnected()
        trialStarter.isTrialNoticePending()
        settingsDataSource.trialStartReadFailure = IOException("disk gone")

        assertFalse(trialStarter.isTrialNoticePending())
    }

}

/**
 * A `SettingsDataSource` whose trial start can be held up, counted, or made to fail.
 *
 * Everything else is the usual fake.
 */
private class TrialSettingsDataSource(
    private val delegate: FakeSettingsDataSource = FakeSettingsDataSource()
) : SettingsDataSource by delegate {

    /**
     * How often the start of the trial has been written.
     */
    var trialStartWrites = 0
        private set

    /**
     * What the next write of the trial start throws, if anything.
     */
    var trialStartWriteFailure: IOException? = null

    /**
     * What the next read of the trial start throws, if anything.
     */
    var trialStartReadFailure: IOException? = null

    private var trialStartGate: CompletableDeferred<Unit>? = null

    /**
     * Keep writes of the trial start waiting until `releaseTrialStartWrites()` is called.
     */
    fun holdTrialStartWrites() {
        trialStartGate = CompletableDeferred()
    }

    /**
     * Let the held-up writes of the trial start through.
     */
    fun releaseTrialStartWrites() {
        trialStartGate?.complete(Unit)
    }

    override val trialStartedAtFlow: Flow<Long?> = flow {
        trialStartReadFailure?.let { throw it }
        emitAll(delegate.trialStartedAtFlow)
    }

    override suspend fun setTrialStartedAt(trialStartedAt: Long?) {
        trialStartWrites++
        trialStartGate?.await()
        trialStartWriteFailure?.let { throw it }
        delegate.setTrialStartedAt(trialStartedAt)
    }

}
