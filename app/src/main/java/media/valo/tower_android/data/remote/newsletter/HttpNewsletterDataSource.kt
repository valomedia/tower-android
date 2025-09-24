/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.newsletter

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
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
    private val contactsOnlyUrl = "https://102627ed.sibforms.com/serve/MUIFABpOIcdIknGqDtcamMRgjIcj9BJT-Ewr9joLZM1SmpFL9Dm7o-AnSF0gekc1XtqqeMbvpWNTq_CdMr1bvKFSIzfpJeinXT0F30qsq-yBrmM3SxSCqPUW4IWdXCPoD-j6U7WjoYqx8M8y-1Kjah_hHDta4lVCNY1y5iD3TZxyL98_vyyXPeCO_2qykfkoeGTW2DzcUTMCZ6uV"

    override suspend fun subscribe(wantsNewsletter: Boolean): Boolean {
        val targetUrl = if (wantsNewsletter) url else contactsOnlyUrl
        try {
            httpClient.submitForm(
                url = targetUrl,
                formParameters = Parameters.Companion.build {
                    profileRepository.getFirstName()?.let { append("VORNAME", it) }
                    profileRepository.getLastName()?.let { append("NACHNAME", it) }
                    profileRepository.getEmail()?.let { append("EMAIL", it) }
                }
            )
            return true
        } catch (e: Exception) {
            return false
        }
    }
}
