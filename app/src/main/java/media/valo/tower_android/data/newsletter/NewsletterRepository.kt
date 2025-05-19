/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.newsletter

import javax.inject.Inject

//
//  NewsletterRepository.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

/**
 * A repository for the Newsletter signup api.
 *
 * @param newsletterDataSource   `newsletterDataSource` dependency.
 */
class NewsletterRepository @Inject constructor(
    private val newsletterDataSource: NewsletterDataSource
) {

    suspend fun subscribe() = newsletterDataSource.subscribe()

}
