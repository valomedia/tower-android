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
//  BillingDataSource.kt
//  Tower_Android
//

import media.valo.tower_android.model.SubscriptionOffer

/**
 * A data source representing the store the subscriptions are sold through.
 */
interface BillingDataSource {

    /**
     * Fetch what the store has to say about the subscription tiers.
     *
     * Tiers the store does not know about, or does not want to tell us about, are left out, so
     * the result can be shorter than the list of tiers, down to empty.
     *
     * @return The offers for the tiers the store knows about.
     *
     * @throws BillingException if the store cannot be reached or refuses the request.
     */
    suspend fun getSubscriptionOffers(): List<SubscriptionOffer>

}

/**
 * The store could not be asked about the subscriptions.
 *
 * @param message   What went wrong, including the store's own debug message where there is one.
 * @param cause     What was thrown underneath, if anything was.
 */
class BillingException(message: String, cause: Throwable? = null) : Exception(message, cause)
