/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

import android.os.Looper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//
//  LooperModule.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

@Module
@InstallIn(SingletonComponent::class)
class LooperModule {

    @Provides
    @Singleton
    fun provideLooper() = Looper.getMainLooper()

}
