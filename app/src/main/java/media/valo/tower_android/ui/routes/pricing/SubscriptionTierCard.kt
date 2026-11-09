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
//  SubscriptionTierCard.kt
//  Tower_Android
//

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import media.valo.tower_android.R
import media.valo.tower_android.model.SubscriptionOffer
import media.valo.tower_android.model.SubscriptionTier
import media.valo.tower_android.ui.theme.Lilac
import media.valo.tower_android.ui.theme.Saffron
import media.valo.tower_android.ui.theme.TowerTheme
import media.valo.tower_android.ui.theme.Verdigris

/**
 * The color a tier is coded with.
 *
 * This is only ever used for graphical elements such as outlines, never for text.
 */
private val SubscriptionTier.color: Color
    get() = when (this) {
        SubscriptionTier.MINI -> Saffron
        SubscriptionTier.BASIC -> Lilac
        SubscriptionTier.POWER -> Verdigris
    }

/**
 * The graphic with the tier's name on it.
 */
private val SubscriptionTier.image: Int
    @DrawableRes
    get() = when (this) {
        SubscriptionTier.MINI -> R.drawable.subscription_mini
        SubscriptionTier.BASIC -> R.drawable.subscription_basic
        SubscriptionTier.POWER -> R.drawable.subscription_power
    }

/**
 * A card presenting one subscription tier.
 *
 * The card is outlined in the tier's color and shows the tier's graphic in a circular crop. The
 * name, description and price come from the store, so the card falls back to what it knows on
 * its own while they are loading or when the store has nothing to say about the tier.
 *
 * @param tier      The tier to present.
 * @param offer     What the store has to say about the tier, if anything.
 * @param isLoading Whether the store is still being asked.
 * @param modifier  `Modifier` for this element.
 */
@Composable
fun SubscriptionTierCard(
    tier: SubscriptionTier,
    offer: SubscriptionOffer?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        border = BorderStroke(2.dp, tier.color),
        modifier = modifier.semantics(mergeDescendants = true) {}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = tier.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, tier.color, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = offer?.name ?: tier.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${tier.minutesPerMonth} Minuten pro Monat",
                    style = MaterialTheme.typography.bodyMedium
                )
                val description = offer?.description
                if (!description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        offer != null -> "${offer.formattedPrice} pro Monat"
                        isLoading -> "Preis wird geladen…"
                        else -> "Preis derzeit nicht verfügbar"
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

/**
 * `Preview` for `SubscriptionTierCard`.
 */
@Preview(showBackground = true, locale = "de-rDE")
@Composable
fun SubscriptionTierCardPreview() {
    TowerTheme {
        Column {
            SubscriptionTierCard(
                tier = SubscriptionTier.MINI,
                offer = SubscriptionOffer(
                    tier = SubscriptionTier.MINI,
                    name = "Mini",
                    description = "Für den gelegentlichen Bedarf.",
                    formattedPrice = "9,99 €"
                ),
                isLoading = false,
                modifier = Modifier.padding(8.dp)
            )
            SubscriptionTierCard(
                tier = SubscriptionTier.BASIC,
                offer = null,
                isLoading = true,
                modifier = Modifier.padding(8.dp)
            )
            SubscriptionTierCard(
                tier = SubscriptionTier.POWER,
                offer = null,
                isLoading = false,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
