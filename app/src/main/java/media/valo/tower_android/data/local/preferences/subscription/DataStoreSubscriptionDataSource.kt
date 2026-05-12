/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.subscription

//
//  DataStoreSubscriptionDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import media.valo.tower_android.utils.SubscriptionDataStore
import media.valo.tower_android.utils.get
import media.valo.tower_android.utils.set
import javax.inject.Inject

/**
 * A `SubscriptionDataSource` backed by a `DataStore`.
 *
 * @param dataStore `DataStore` dependency.
 */
class DataStoreSubscriptionDataSource @Inject constructor(
    @SubscriptionDataStore private val dataStore: DataStore<Preferences>
) : SubscriptionDataSource {

    private val statusKey: Preferences.Key<String> = stringPreferencesKey("status")
    private val productIdKey: Preferences.Key<String> = stringPreferencesKey("productId")
    private val purchaseTokenKey: Preferences.Key<String> = stringPreferencesKey("purchaseToken")
    private val lastUpdatedAtMillisKey: Preferences.Key<Long> =
        longPreferencesKey("lastUpdatedAtMillis")

    override val statusFlow: Flow<String?> = dataStore.get(statusKey)

    override val productIdFlow: Flow<String?> = dataStore.get(productIdKey)

    override val purchaseTokenFlow: Flow<String?> = dataStore.get(purchaseTokenKey)

    override val lastUpdatedAtMillisFlow: Flow<Long?> = dataStore.get(lastUpdatedAtMillisKey)

    override suspend fun setStatus(status: String?) = dataStore.set(statusKey, status)

    override suspend fun setProductId(productId: String?) = dataStore.set(productIdKey, productId)

    override suspend fun setPurchaseToken(purchaseToken: String?) =
        dataStore.set(purchaseTokenKey, purchaseToken)

    override suspend fun setLastUpdatedAtMillis(lastUpdatedAtMillis: Long?) =
        dataStore.set(lastUpdatedAtMillisKey, lastUpdatedAtMillis)
}
