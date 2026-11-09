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
//  SubscriptionOffer.kt
//  Tower_Android
//

/**
 * What Google Play has to say about a subscription tier.
 *
 * @property tier           The tier the offer is for.
 * @property name           The display name of the subscription from the Google Play Console.
 * @property description    The description of the subscription from the Google Play Console.
 * @property formattedPrice The monthly price, formatted for the user's locale and currency.
 */
data class SubscriptionOffer(
    val tier: SubscriptionTier,
    val name: String,
    val description: String,
    val formattedPrice: String
)
