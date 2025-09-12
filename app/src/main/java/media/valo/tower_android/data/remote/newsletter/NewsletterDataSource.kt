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
//  Created by:
//      * mvlexs
//
/**
 * A data source representing the Newsletter signup api.
 */
interface NewsletterDataSource {

    /**
     * Make a post to the newsletter endpoint, containing the users first name, email address and second name if set.
     *
     * This will make a post to the newsletter endpoint, returning 'true' when the call succeeds (meaning
     * the user got signed up for the newsletter), and throwing + returning false otherwise.
     */
    suspend fun subscribe(): Boolean

    /**
     * Make a post to the contacts-only endpoint, containing the users first name, email address and second name if set.
     *
     * This will make a post to the endpoint, returning 'true' when the call succeeds, and throwing + returning false otherwise.
     */
    suspend fun addContactOnly(): Boolean

}
