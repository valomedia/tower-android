/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.loading

//
//  LoadingDataSourceModule.kt
//  Tower_Android
//
//  Created by:
//      * Jan Hofherr
//

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LoadingDataSourceModule {

    @Binds
    fun bindLoadingDataSource(
        dataStoreLoadingDataSource: DataStoreLoadingDataSource
    ): LoadingDataSource

}
