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

package media.valo.tower_android

//
//  FakeBillingDataSource.kt
//  Tower_Android
//

import media.valo.tower_android.data.remote.billing.BillingDataSource
import media.valo.tower_android.data.remote.billing.BillingException
import media.valo.tower_android.model.SubscriptionOffer

/**
 * A fake implementation of `BillingDataSource`.
 *
 * This answers from memory, so that tests can put the store into a given state.
 *
 * @param offers    The offers to answer with, or `null` to fail the way an unreachable store would.
 */
class FakeBillingDataSource(
    var offers: List<SubscriptionOffer>? = emptyList()
) : BillingDataSource {

    override suspend fun getSubscriptionOffers(): List<SubscriptionOffer> =
        offers ?: throw BillingException("The store could not be reached.")

}
