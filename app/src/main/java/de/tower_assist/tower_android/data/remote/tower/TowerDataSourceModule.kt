/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.data.remote.tower

//
//  TowerDataSourceModule.kt
//  Tower_Android
//

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TowerDataSourceModule {

    @Binds
    fun bindTowerDataSource(
        httpTowerDataSource: HttpTowerDataSource
    ): TowerDataSource

}
