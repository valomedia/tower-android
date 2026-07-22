/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.settings

//
//  SettingsDataSourceModule.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import media.valo.tower_android.data.local.preferences.loading.DataStoreLoadingDataSource
import media.valo.tower_android.data.local.preferences.loading.LoadingDataSource

@Module
@InstallIn(SingletonComponent::class)
interface SettingsDataSourceModule {

    @Binds
    fun bindSettingsDataSource(
        dataStoreSettingsDataSource: DataStoreSettingsDataSource
    ): SettingsDataSource
}
