/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.newsletter

//
//  DummyHttpNewsletterDataSource.kt
//  Tower_Android
//

/**
 * A dummy implementation of `NewsletterDataSource`.
 *
 * This implements all api-calls as no-ops.
 */
class DummyNewsletterDataSource: NewsletterDataSource {

    override suspend fun subscribe(wantsNewsletter: Boolean): Boolean = false

}
