/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.data.local.preferences.credentials

//
//  CredentialDataSourceModule.kt
//  Tower_Android
//

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CredentialDataSourceModule {

    @Binds
    fun bindCredentialsDataSource(
        dataStoreCredentialsDataSource: DataStoreCredentialDataSource
    ): CredentialDataSource

}
