/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.video

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

//
//  VideoDataSourceModule.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

@Module
@InstallIn(SingletonComponent::class)
interface VideoDataSourceModule {

    @Binds
    fun bindVideoDataSource(
        cameraVideoDataSource: CameraVideoDataSource
    ): VideoDataSource

}
