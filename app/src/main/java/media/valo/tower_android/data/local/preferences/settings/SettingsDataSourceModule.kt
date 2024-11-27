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

@Module
@InstallIn(SingletonComponent::class)
interface SettingsDataSourceModule {

    @Binds
    fun bindSettingsDataSource(
        dataStoreSettingsDataSource: DataStoreSettingsDataSource
    ): SettingsDataSource

}
