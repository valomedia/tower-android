/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.elements

//
//  SubscriptionUi.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import media.valo.tower_android.billing.EntitlementVerificationStatus
import media.valo.tower_android.billing.SubscriptionUiState
import media.valo.tower_android.model.SubscriptionStatus
import media.valo.tower_android.ui.theme.towerTextStyle

private const val PENDING_SUBSCRIPTION_MESSAGE =
    "Dein Kauf wird noch von Google Play bestätigt. Der Zugang wird danach automatisch freigeschaltet."
private const val VERIFYING_CACHED_ENTITLEMENT_MESSAGE =
    "Dein zuletzt bekannter Abo-Status wird gerade mit Google Play abgeglichen. Neue Calls bleiben bis zum Abschluss der Prüfung gesperrt."
private const val FAILED_CACHED_ENTITLEMENT_MESSAGE =
    "Dein zuletzt lokal gespeicherter Abo-Status ist aktiv, konnte aber gerade nicht bestätigt werden. Bitte versuche es erneut oder prüfe dein Abo in Google Play."

fun SubscriptionUiState.subscriptionStatusText(): String = when {
    isEntitled -> "Status: Aktiv"
    hasUnverifiedCachedEntitlement &&
        verificationStatus == EntitlementVerificationStatus.VERIFYING ->
        "Status: Abo wird überprüft"
    hasUnverifiedCachedEntitlement -> "Status: Letzter bekannter Stand aktiv"
    status == SubscriptionStatus.PENDING -> "Status: Zahlung ausstehend"
    else -> "Status: Nicht freigeschaltet"
}

fun SubscriptionUiState.cachedEntitlementNoticeText(): String? {
    if (!hasUnverifiedCachedEntitlement) {
        return null
    }

    return when (verificationStatus) {
        EntitlementVerificationStatus.VERIFYING -> VERIFYING_CACHED_ENTITLEMENT_MESSAGE
        EntitlementVerificationStatus.FAILED -> FAILED_CACHED_ENTITLEMENT_MESSAGE
        // VERIFIED is not expected while the UI is still relying on cached entitlement state.
        EntitlementVerificationStatus.VERIFIED -> null
    }
}

fun pendingSubscriptionNoticeText(): String = PENDING_SUBSCRIPTION_MESSAGE

@Composable
fun SubscriptionSection(
    subscriptionState: SubscriptionUiState,
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
    onManageSubscription: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "26",
            style = towerTextStyle,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = subscriptionState.planName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = if (subscriptionState.isEntitled) {
                "Dein Zugang im TOWER ist freigeschaltet."
            } else {
                "Ein Launch-Paket, das den Call-Zugang im TOWER freischaltet."
            },
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SubscriptionFactRow(
                    label = "Status",
                    value = subscriptionState.subscriptionStatusText()
                )
                SubscriptionFactRow(
                    label = "Testphase",
                    value = subscriptionState.trialLabel,
                    modifier = Modifier.padding(top = 8.dp)
                )
                SubscriptionFactRow(
                    label = "Enthalten",
                    value = subscriptionState.minutesLabel,
                    modifier = Modifier.padding(top = 8.dp)
                )
                SubscriptionFactRow(
                    label = "Danach",
                    value = subscriptionState.monthlyPriceLabel,
                    modifier = Modifier.padding(top = 8.dp)
                )
                SubscriptionFactRow(
                    label = "Vertrag",
                    value = subscriptionState.billingPeriodLabel,
                    modifier = Modifier.padding(top = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text(text = subscriptionState.launchOfferLabel)
                Text(
                    text = subscriptionState.pricingDisclaimer,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        if (subscriptionState.status == SubscriptionStatus.PENDING) {
            SubscriptionNoticeCard(
                text = pendingSubscriptionNoticeText(),
                contentDescription = "Ausstehend"
            )
        }
        subscriptionState.cachedEntitlementNoticeText()?.let { noticeText ->
            SubscriptionNoticeCard(
                text = noticeText,
                contentDescription = "Abo-Prüfung"
            )
        }
        if (!subscriptionState.errorMessage.isNullOrBlank()) {
            Text(
                text = subscriptionState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
        FilledTonalButton(
            onClick = onPurchase,
            enabled = subscriptionState.canPurchase,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                if (subscriptionState.isEntitled) {
                    "Abo aktiv"
                } else {
                    "26 Tage kostenlos starten"
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onRestore,
                enabled = !subscriptionState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abo wiederherstellen")
            }
            OutlinedButton(
                onClick = onManageSubscription,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abo verwalten")
            }
        }
    }
}

@Composable
fun SubscriptionNoticeCard(
    text: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Lock, contentDescription = contentDescription)
            Text(
                text = text,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
private fun SubscriptionFactRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value)
    }
}

fun Context.openSubscriptionManagement(productId: String) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "https://play.google.com/store/account/subscriptions?sku=$productId&package=$packageName"
            )
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}
