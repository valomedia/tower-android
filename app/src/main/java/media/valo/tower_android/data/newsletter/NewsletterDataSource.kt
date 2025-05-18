/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.newsletter

//
//  NewsletterDataSource.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

interface NewsletterDataSource {

    suspend fun subscribe(): Boolean

}
