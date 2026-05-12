/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.billing

//
//  OfferSelectionTest.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OfferSelectionTest {

    @Test
    fun configuredBasePlanAndOfferTakePriority() {
        val candidates = listOf(
            OfferSelectionSnapshot(
                offerToken = "fallback",
                basePlanId = "launch",
                offerId = "trial",
                hasFreePhase = true,
                pricingPhaseSignature = "P26D:0|P1M:26000000"
            ),
            OfferSelectionSnapshot(
                offerToken = "configured",
                basePlanId = "standard",
                offerId = "intro",
                hasFreePhase = false,
                pricingPhaseSignature = "P1M:29000000"
            )
        )

        val selected = selectOfferSnapshot(
            candidates = candidates,
            config = OfferSelectionConfig(basePlanId = "standard", offerId = "intro")
        )

        assertEquals("configured", selected?.offerToken)
    }

    @Test
    fun configuredIdsMustMatchExistingOffer() {
        val candidates = listOf(
            OfferSelectionSnapshot(
                offerToken = "launch",
                basePlanId = "launch",
                offerId = "trial",
                hasFreePhase = true,
                pricingPhaseSignature = "P26D:0|P1M:26000000"
            )
        )

        val selected = selectOfferSnapshot(
            candidates = candidates,
            config = OfferSelectionConfig(basePlanId = "missing")
        )

        assertNull(selected)
    }

    @Test
    fun fallbackSelectionIsDeterministicWithoutConfiguredIds() {
        val candidates = listOf(
            OfferSelectionSnapshot(
                offerToken = "zeta",
                basePlanId = "zeta-plan",
                offerId = "trial",
                hasFreePhase = true,
                pricingPhaseSignature = "P26D:0|P1M:26000000"
            ),
            OfferSelectionSnapshot(
                offerToken = "alpha",
                basePlanId = "alpha-plan",
                offerId = "trial",
                hasFreePhase = true,
                pricingPhaseSignature = "P26D:0|P1M:26000000"
            )
        )

        val selected = selectOfferSnapshot(
            candidates = candidates,
            config = OfferSelectionConfig()
        )

        assertEquals("alpha", selected?.offerToken)
    }
}
