/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.newsletter

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.utils.NewsletterHttpClient
import javax.inject.Inject

//
//  HttpNewsletterDataSource.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

class HttpNewsletterDataSource @Inject constructor(
    private val profileRepository: ProfileRepository,
    @NewsletterHttpClient private val httpClient: HttpClient
): NewsletterDataSource {
    private val url = "https://102627ed.sibforms.com/serve/MUIFAEchGCPcZdL9j3YFGC6VsahIbu0_oP1O2xv5eqY_utQUNu0eiFdF_FkAtpwjROfWOY2c__ltTpJ4DnZprEhfJsD8pnGK9V3nSaxEhEXTyNeHNqzZy7SWT9OF1t6Qr7ud9YipcpzI4YoG3TRP7QtFN1HBNWE26Vb3YUc06M8QwQsVD6WXFnAampEWhkp3tYf8EdFUfB6n_TYL"

    override suspend fun subscribe() {
        httpClient.submitForm(
            url = url,
            formParameters = Parameters.Companion.build {
                profileRepository.getEmail()?.let { append("EMAIL", it) }
                profileRepository.getFirstName()?.let { append("VORNAME", it) }
                if (profileRepository.getLastName() != null){
                    profileRepository.getLastName()?.let { append("NACHNAME", it) }
                }
            }
        )
    }
}