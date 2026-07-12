/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.newsletter

import javax.inject.Inject

/**
 * A repository for the Newsletter signup api.
 *
 * @param newsletterDataSource   `newsletterDataSource` dependency.
 */
class NewsletterRepository @Inject constructor(
    private val newsletterDataSource: NewsletterDataSource
) {

    /**
     * Make a post to the appropriate endpoint, containing the users first name, email adress and second name if set.
     *
     * If `wantsNewsletter` is `true`, this posts to the newsletter endpoint (user subscribes).
     * If `wantsNewsletter` is `false`, this posts to the contacts-only endpoint.
     */
    suspend fun subscribe(wantsNewsletter: Boolean) = newsletterDataSource.subscribe(wantsNewsletter)

}
