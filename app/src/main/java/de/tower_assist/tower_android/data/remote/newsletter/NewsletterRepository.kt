/******************************************************************************
 * Copyright (c) 2025-2026 valo.media GmbH                                    *
 * All rights reserved.                                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Affero General Public License as             *
 * published by the Free Software Foundation, either version 3 of the         *
 * License, or (at your option) any later version.                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Affero General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Affero General Public License   *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package de.tower_assist.tower_android.data.remote.newsletter

import javax.inject.Inject

//
//  NewsletterRepository.kt
//  Tower_Android
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
     * Make a post to the appropriate endpoint, containing the users first name, email adress and second name if set.
     *
     * If `wantsNewsletter` is `true`, this posts to the newsletter endpoint (user subscribes).
     * If `wantsNewsletter` is `false`, this posts to the contacts-only endpoint.
     */
    suspend fun subscribe(wantsNewsletter: Boolean) = newsletterDataSource.subscribe(wantsNewsletter)

}
