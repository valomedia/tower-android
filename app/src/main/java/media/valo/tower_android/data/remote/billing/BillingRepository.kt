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

package media.valo.tower_android.data.remote.billing

//
//  BillingRepository.kt
//  Tower_Android
//

import media.valo.tower_android.model.SubscriptionOffer
import media.valo.tower_android.model.SubscriptionTier
import javax.inject.Inject

/**
 * A repository for the store the subscriptions are sold through.
 *
 * @param billingDataSource `BillingDataSource` dependency.
 */
class BillingRepository @Inject constructor(
    private val billingDataSource: BillingDataSource
) {

    /**
     * Fetch what the store has to say about the subscription tiers.
     *
     * @return The offers by tier, for the tiers the store knows about.
     *
     * @throws BillingException if the store cannot be reached or refuses the request.
     */
    suspend fun getSubscriptionOffers(): Map<SubscriptionTier, SubscriptionOffer> =
        billingDataSource.getSubscriptionOffers().associateBy { it.tier }

}
