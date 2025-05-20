/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.newsletter

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

    /**
     * Make a post to the newsletter endpoint, containing the users first name, email adress and second name if set.
     *
     * This will make a post to the newsletter endpoint, returning 'true' when the call succeeds (meaning
     * the user got signed up for the newsletter), and throwing + returning false otherwise.
     */
    suspend fun subscribe() = newsletterDataSource.subscribe()

}
