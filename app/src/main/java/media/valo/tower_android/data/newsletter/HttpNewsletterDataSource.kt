/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.newsletter

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
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

/**
 * A `NewsletterDataSource` backed by a `HttpClient`.
 *
 * @param profileRepository    `ProfileRepository` dependency.
 * @param httpClient            `HttpClient` dependency.
 */
class HttpNewsletterDataSource @Inject constructor(
    private val profileRepository: ProfileRepository,
    @NewsletterHttpClient private val httpClient: HttpClient
): NewsletterDataSource {
    private val url = "https://102627ed.sibforms.com/serve/MUIFAEchGCPcZdL9j3YFGC6VsahIbu0_oP1O2xv5eqY_utQUNu0eiFdF_FkAtpwjROfWOY2c__ltTpJ4DnZprEhfJsD8pnGK9V3nSaxEhEXTyNeHNqzZy7SWT9OF1t6Qr7ud9YipcpzI4YoG3TRP7QtFN1HBNWE26Vb3YUc06M8QwQsVD6WXFnAampEWhkp3tYf8EdFUfB6n_TYL"

    override suspend fun subscribe(): Boolean {
        return try {
            val response = httpClient.submitForm(
                url = url,
                formParameters = Parameters.Companion.build {
                    profileRepository.getFirstName()?.let { append("VORNAME", it) }
                    profileRepository.getLastName()?.let { append("NACHNAME", it) }
                    profileRepository.getEmail()?.let { append("EMAIL", it) }
                }
            )
            Log.d("Malik", response.status.isSuccess().toString())
            response.status.isSuccess()
        } catch (e: Exception) {
            Log.d("Malik", e.toString())
            return false
        }
    }


}
