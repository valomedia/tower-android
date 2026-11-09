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

package media.valo.tower_android.ui.routes.pricing

//
//  PricingViewModel.kt
//  Tower_Android
//

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.data.remote.billing.BillingException
import media.valo.tower_android.data.remote.billing.BillingRepository
import media.valo.tower_android.model.SubscriptionOffer
import media.valo.tower_android.model.SubscriptionTier
import media.valo.tower_android.utils.AppScope
import java.io.IOException
import javax.inject.Inject

/**
 * Tag added to log messages related to the PricingViewModel.
 */
private const val TAG = "PricingViewModel"

/**
 * `ViewModel` for `PricingScreen`.
 *
 * The offers are fetched from the store once, when the `ViewModel` is created, so that the screen
 * being rebuilt (on rotation, say) does not ask the store again.
 *
 * @param billingRepository     `BillingRepository` dependency.
 * @param settingsRepository    `SettingsRepository` dependency.
 * @param appScope              Scope for work that has to outlive this screen.
 */
@HiltViewModel
class PricingViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
    private val settingsRepository: SettingsRepository,
    @AppScope private val appScope: CoroutineScope
) : ViewModel() {

    private val offersByTier = mutableStateMapOf<SubscriptionTier, SubscriptionOffer>()

    /**
     * Whether the offers are still being fetched from the store.
     */
    var isLoading by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch { loadOffers() }
    }

    /**
     * What the store has to say about a tier.
     *
     * @param tier  The tier to look up.
     * @return      The offer for the tier, or `null` while loading or if the store has none.
     */
    fun offerFor(tier: SubscriptionTier): SubscriptionOffer? = offersByTier[tier]

    /**
     * Record that the user has been told that their first call started the free trial.
     *
     * The notice is owed from the first call until this is called, so that a first call ending out
     * of sight (in picture-in-picture, say) still gets its notice on the next start.
     *
     * The write runs on the app scope rather than this `ViewModel`'s own, because both the screen
     * and the `ViewModel` go away on a back press, and a notice that was shown should stay seen
     * however quickly the user left. The settings being unwritable is not worth taking the screen
     * down over, so that is only logged, and the next start shows the notice again.
     */
    fun markTrialNoticeSeen() {
        appScope.launch {
            try {
                settingsRepository.markTrialNoticeSeen()
            } catch (e: IOException) {
                Log.w(TAG, "Could not record the trial notice as seen", e)
            }
        }
    }

    /**
     * Fetch the offers from the store.
     *
     * The store not answering is only logged, and the tiers are shown without a price. The store
     * is asked again the next time the screen is opened, which is as much of a retry as there is
     * for now.
     *
     * A tier the store answered without is logged as well, because a store that answers but
     * leaves a tier out (one that is not published yet, say) looks the same on the screen as one
     * that could not be reached, and would otherwise leave no trace.
     */
    private suspend fun loadOffers() {
        try {
            offersByTier.putAll(billingRepository.getSubscriptionOffers())
            logTiersWithoutOffer()
        } catch (e: BillingException) {
            Log.w(TAG, "Could not load the subscription offers", e)
        } finally {
            isLoading = false
        }
    }

    private fun logTiersWithoutOffer() {
        for (tier in SubscriptionTier.entries - offersByTier.keys) {
            Log.w(TAG, "The store returned no offer for $tier (${tier.productId})")
        }
    }

}
