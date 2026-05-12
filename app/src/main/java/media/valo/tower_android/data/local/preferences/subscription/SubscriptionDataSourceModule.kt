/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.subscription

//
//  SubscriptionDataSourceModule.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SubscriptionDataSourceModule {

    @Binds
    fun bindSubscriptionDataSource(
        dataStoreSubscriptionDataSource: DataStoreSubscriptionDataSource
    ): SubscriptionDataSource
}
