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
//  SubscriptionTier.kt
//  Tower_Android
//

/**
 * How many minutes of assistance every user gets for free each month, with or without a
 * subscription.
 */
const val FREE_MINUTES_PER_MONTH = 5

/**
 * The subscriptions TOWER offers, smallest first.
 *
 * Each tier is a monthly subscription set up as a product in the Google Play Console with a single
 * monthly base plan. The display name, description and price come from there, so only what Google
 * Play does not know about a tier lives here.
 *
 * @property productId          The id of the product in the Google Play Console.
 * @property title              What to call the tier when Google Play has not told us its name.
 * @property minutesPerMonth    How many minutes of assistance the tier includes per month.
 */
enum class SubscriptionTier(
    val productId: String,
    val title: String,
    val minutesPerMonth: Int
) {

    MINI(productId = "media.valo.tower_android.mini", title = "Mini", minutesPerMonth = 20),

    BASIC(productId = "media.valo.tower_android.basic", title = "Basic", minutesPerMonth = 50),

    POWER(productId = "media.valo.tower_android.power", title = "Power", minutesPerMonth = 100);

    companion object {

        /**
         * Find the tier sold under a product id.
         *
         * @param productId The id of the product in the Google Play Console.
         * @return          The tier sold under that id, or `null` if the id belongs to no tier.
         */
        fun fromProductId(productId: String): SubscriptionTier? =
            entries.firstOrNull { it.productId == productId }

    }

}
