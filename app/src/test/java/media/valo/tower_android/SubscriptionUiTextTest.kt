/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

//
//  SubscriptionUiTextTest.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import media.valo.tower_android.billing.EntitlementVerificationStatus
import media.valo.tower_android.billing.SubscriptionUiState
import media.valo.tower_android.model.SubscriptionStatus
import media.valo.tower_android.ui.elements.cachedEntitlementNoticeText
import media.valo.tower_android.ui.elements.pendingSubscriptionNoticeText
import media.valo.tower_android.ui.elements.subscriptionStatusText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SubscriptionUiTextTest {

    @Test
    fun verifyingCachedEntitlementUsesSharedStatusAndNoticeCopy() {
        val state = SubscriptionUiState(
            isLoading = true,
            status = SubscriptionStatus.ACTIVE,
            verificationStatus = EntitlementVerificationStatus.VERIFYING,
            isUsingCachedEntitlement = true
        )

        assertEquals("Status: Abo wird überprüft", state.subscriptionStatusText())
        assertEquals(
            "Dein zuletzt bekannter Abo-Status wird gerade mit Google Play abgeglichen. Neue Calls bleiben bis zum Abschluss der Prüfung gesperrt.",
            state.cachedEntitlementNoticeText()
        )
    }

    @Test
    fun verifiedCachedEntitlementDoesNotExposeDeadNoticeCopy() {
        val state = SubscriptionUiState(
            isLoading = false,
            status = SubscriptionStatus.ACTIVE,
            verificationStatus = EntitlementVerificationStatus.VERIFIED,
            isUsingCachedEntitlement = true
        )

        assertEquals("Status: Letzter bekannter Stand aktiv", state.subscriptionStatusText())
        assertNull(state.cachedEntitlementNoticeText())
    }

    @Test
    fun pendingNoticeCopyIsShared() {
        assertEquals(
            "Dein Kauf wird noch von Google Play bestätigt. Der Zugang wird danach automatisch freigeschaltet.",
            pendingSubscriptionNoticeText()
        )
    }
}
