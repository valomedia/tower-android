/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

//
//  SubscriptionUiStateTest.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import media.valo.tower_android.billing.EntitlementVerificationStatus
import media.valo.tower_android.billing.SubscriptionUiState
import media.valo.tower_android.model.SubscriptionStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionUiStateTest {

    @Test
    fun cachedActiveSubscriptionDoesNotUnlockCallAccess() {
        val state = SubscriptionUiState(
            isLoading = true,
            status = SubscriptionStatus.ACTIVE,
            verificationStatus = EntitlementVerificationStatus.VERIFYING,
            isUsingCachedEntitlement = true
        )

        assertFalse(state.isEntitled)
        assertTrue(state.hasUnverifiedCachedEntitlement)
        assertFalse(state.canPurchase)
    }

    @Test
    fun verifiedActiveSubscriptionUnlocksCallAccess() {
        val state = SubscriptionUiState(
            isLoading = false,
            status = SubscriptionStatus.ACTIVE,
            verificationStatus = EntitlementVerificationStatus.VERIFIED,
            isBillingAvailable = true,
            isProductAvailable = true,
            isUsingCachedEntitlement = false
        )

        assertTrue(state.isEntitled)
        assertFalse(state.hasUnverifiedCachedEntitlement)
        assertFalse(state.canPurchase)
    }

    @Test
    fun pendingSubscriptionKeepsPurchaseAndCallAccessLocked() {
        val state = SubscriptionUiState(
            isLoading = false,
            status = SubscriptionStatus.PENDING,
            verificationStatus = EntitlementVerificationStatus.VERIFIED,
            isBillingAvailable = true,
            isProductAvailable = true
        )

        assertFalse(state.isEntitled)
        assertFalse(state.canPurchase)
    }

    @Test
    fun unsubscribedVerifiedUserCanPurchase() {
        val state = SubscriptionUiState(
            isLoading = false,
            status = SubscriptionStatus.NONE,
            verificationStatus = EntitlementVerificationStatus.VERIFIED,
            isBillingAvailable = true,
            isProductAvailable = true
        )

        assertFalse(state.isEntitled)
        assertTrue(state.canPurchase)
    }
}
