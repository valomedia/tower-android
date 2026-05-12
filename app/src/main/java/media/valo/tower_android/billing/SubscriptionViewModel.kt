/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.billing

//
//  SubscriptionViewModel.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import android.app.Activity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val billingRepository: TowerBillingRepository
) : ViewModel() {

    val uiState: StateFlow<SubscriptionUiState> = billingRepository.uiState

    fun refresh() = billingRepository.refresh()

    fun restorePurchases() = billingRepository.restorePurchases()

    fun launchPurchaseFlow(activity: Activity) = billingRepository.launchPurchaseFlow(activity)
}
