/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.newsletter

//
//  DummyHttpNewsletterDataSource.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//


class DummyHttpNewsletterDataSource: NewsletterDataSource {

    override suspend fun subscribe() = Unit

}
