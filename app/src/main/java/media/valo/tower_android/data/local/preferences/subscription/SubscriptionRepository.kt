/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.subscription

//
//  SubscriptionRepository.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import kotlinx.coroutines.flow.firstOrNull
import media.valo.tower_android.model.SubscriptionStatus
import javax.inject.Inject

data class CachedSubscription(
    val status: SubscriptionStatus = SubscriptionStatus.NONE,
    val productId: String? = null,
    val purchaseToken: String? = null,
    val lastUpdatedAtMillis: Long? = null
)

/**
 * Repository for the locally cached subscription status.
 *
 * @param subscriptionDataSource `SubscriptionDataSource` dependency.
 */
class SubscriptionRepository @Inject constructor(
    private val subscriptionDataSource: SubscriptionDataSource
) {

    suspend fun getCachedSubscription(): CachedSubscription {
        val status = subscriptionDataSource.statusFlow.firstOrNull()
            ?.let { runCatching { SubscriptionStatus.valueOf(it) }.getOrNull() }
            ?: SubscriptionStatus.NONE
        return CachedSubscription(
            status = status,
            productId = subscriptionDataSource.productIdFlow.firstOrNull(),
            purchaseToken = subscriptionDataSource.purchaseTokenFlow.firstOrNull(),
            lastUpdatedAtMillis = subscriptionDataSource.lastUpdatedAtMillisFlow.firstOrNull()
        )
    }

    suspend fun setCachedSubscription(
        status: SubscriptionStatus,
        productId: String?,
        purchaseToken: String?
    ) {
        subscriptionDataSource.setStatus(status.name)
        subscriptionDataSource.setProductId(productId)
        subscriptionDataSource.setPurchaseToken(purchaseToken)
        subscriptionDataSource.setLastUpdatedAtMillis(System.currentTimeMillis())
    }
}
