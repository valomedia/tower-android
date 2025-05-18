/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.newsletter

//
//  NewsletterRepository.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

import javax.inject.Inject

class NewsletterRepository @Inject constructor(
    private val newsletterDataSource: NewsletterDataSource
) {

    suspend fun subscribe() = newsletterDataSource.subscribe()

}