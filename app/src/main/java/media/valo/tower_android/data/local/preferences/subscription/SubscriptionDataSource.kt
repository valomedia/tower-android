/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.subscription

//
//  SubscriptionDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import kotlinx.coroutines.flow.Flow

/**
 * A data source for the locally cached subscription status.
 */
interface SubscriptionDataSource {

    val statusFlow: Flow<String?>

    val productIdFlow: Flow<String?>

    val purchaseTokenFlow: Flow<String?>

    val lastUpdatedAtMillisFlow: Flow<Long?>

    suspend fun setStatus(status: String?)

    suspend fun setProductId(productId: String?)

    suspend fun setPurchaseToken(purchaseToken: String?)

    suspend fun setLastUpdatedAtMillis(lastUpdatedAtMillis: Long?)
}
