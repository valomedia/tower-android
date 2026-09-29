/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.newsletter

//
//  NewsletterDataSource.kt
//  Tower_Android
//
/**
 * A data source representing the Newsletter signup api.
 */
interface NewsletterDataSource {

    /**
     * Make a post to the appropriate endpoint, containing the users first name, email address and second name if set.
     *
     * If `wantsNewsletter` is `true`, this will post to the newsletter endpoint (user subscribes).
     * If `wantsNewsletter` is `false`, this will post to the contacts-only endpoint.
     *
     * Returns `true` when the call succeeds, and throwing + returning `false` otherwise.
     */
    suspend fun subscribe(wantsNewsletter: Boolean): Boolean

}
