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
//  PricingScreen.kt
//  Tower_Android
//

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import media.valo.tower_android.data.local.preferences.settings.DummySettingsDataSource
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.data.remote.billing.BillingRepository
import media.valo.tower_android.data.remote.billing.DummyBillingDataSource
import media.valo.tower_android.model.FREE_MINUTES_PER_MONTH
import media.valo.tower_android.model.SubscriptionTier
import media.valo.tower_android.model.TRIAL_DURATION_DAYS
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.utils.CoroutineScopeModule

/**
 * Object for the navigation destination for the pricing screen.
 *
 * @param isTrialJustStarted    Whether the screen is shown because the user's first call has just
 *                              started their free trial.
 */
@Serializable
data class PricingScreen(val isTrialJustStarted: Boolean = false)

/**
 * Screen with the subscription tiers and what they cost.
 *
 * @param isTrialJustStarted    Whether to tell the user that their free trial has just started.
 * @param modifier              `Modifier` for this element.
 * @param viewModel             `PricingViewModel` dependency.
 */
@Composable
fun PricingScreen(
    isTrialJustStarted: Boolean,
    modifier: Modifier = Modifier,
    viewModel: PricingViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()

    // Keyed on the argument so that an entry whose argument is replaced in place (see
    // `navigateToPricing()`) still marks the notice seen if it ever flips to `true`.
    LaunchedEffect(isTrialJustStarted) {
        if (isTrialJustStarted) { viewModel.markTrialNoticeSeen() }
    }

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Unsere Abos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        if (isTrialJustStarted) {
            TrialNotice(modifier = Modifier.padding(8.dp).fillMaxWidth())
        }
        // Purchasing is not implemented yet, so this must not send the user looking for a way to
        // pick one of the cards below.
        Text(
            "Jeden Monat bekommst du $FREE_MINUTES_PER_MONTH Minuten Fernassistenz geschenkt, "
                    + "mit oder ohne Abo. Wenn du mehr brauchst, kannst du bald hier eines "
                    + "unserer Abos wählen.",
            modifier = Modifier.padding(8.dp)
        )
        for (tier in SubscriptionTier.entries) {
            SubscriptionTierCard(
                tier = tier,
                offer = viewModel.offerFor(tier),
                isLoading = viewModel.isLoading,
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            )
        }
        Text(
            "Erfolgsgarantie: wenn wir gemeinsam nicht erfolgreich waren, werden keine Minuten "
                    + "verbraucht.",
            modifier = Modifier.padding(8.dp)
        )
    }
}

/**
 * A note telling the user that their free trial has just started.
 *
 * @param modifier  `Modifier` for this element.
 */
@Composable
private fun TrialNotice(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    ) {
        // The notice leaves out that the call was the user's first, because for anyone who had
        // the app before the trial existed it is only their first call since updating.
        Text(
            "Deine kostenlose Testphase hat begonnen und läuft $TRIAL_DURATION_DAYS Tage.",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * Open the pricing screen from somewhere the user chose to go there, such as the menu.
 *
 * The pricing may already be up with the trial notice, which is the same destination with a
 * different argument. Pushing another copy would put the notice behind the back button, so the
 * entry that is up is kept and only its argument is replaced: the notice goes away, having been
 * shown, and back still leads where it led before.
 */
fun NavController.navigateToPricing() {
    navigate(route = PricingScreen()) {
        launchSingleTop = true
    }
}

/**
 * `Preview` for `PricingScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
@SuppressLint("ViewModelConstructorInComposable")
fun PricingScreenPreview() {
    AppBarPreview { innerPadding ->
        PricingScreen(
            isTrialJustStarted = true,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize(),
            viewModel = PricingViewModel(
                billingRepository = BillingRepository(DummyBillingDataSource()),
                settingsRepository = SettingsRepository(DummySettingsDataSource()),
                appScope = CoroutineScopeModule().provideCoroutineScope()
            )
        )
    }
}
