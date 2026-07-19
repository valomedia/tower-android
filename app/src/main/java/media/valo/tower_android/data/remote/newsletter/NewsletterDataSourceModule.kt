/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.newsletter

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

//
//  NewsletterDataSourceModule.kt
//  Tower_Android
//

@Module
@InstallIn(SingletonComponent::class)
interface NewsletterDataSourceModule {

    @Binds
    fun bindNewsletterDataSource(
        httpNewsletterDataSource: HttpNewsletterDataSource
    ): NewsletterDataSource

}
