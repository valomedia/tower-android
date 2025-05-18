/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.tower


import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import media.valo.tower_android.data.local.preferences.profile.ProfileDataSource
import media.valo.tower_android.utils.NewsletterHttpClient
import javax.inject.Inject

//
//  DataStoreProfileDataSource.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

class NewsletterRepository @Inject constructor(
    @NewsletterHttpClient private val httpClient: HttpClient,
    profileDataSource: ProfileDataSource
) {

    private val url = "https://102627ed.sibforms.com/serve/MUIFAEchGCPcZdL9j3YFGC6VsahIbu0_oP1O2xv5eqY_utQUNu0eiFdF_FkAtpwjROfWOY2c__ltTpJ4DnZprEhfJsD8pnGK9V3nSaxEhEXTyNeHNqzZy7SWT9OF1t6Qr7ud9YipcpzI4YoG3TRP7QtFN1HBNWE26Vb3YUc06M8QwQsVD6WXFnAampEWhkp3tYf8EdFUfB6n_TYL"

    /**
     * A `Flow` that emits the first name every time it is updated.
     *
     * This will emit the new name each time it is set. When the name is unset, it will emit `null`.
     */
    val firstNameFlow: Flow<String?> = profileDataSource.firstNameFlow

    /**
     * Get the first name (if any).
     *
     * @return The first name that is currently set, or `null` if the first name is unset.
     */
    suspend fun getFirstName(): String? = firstNameFlow.firstOrNull()

    /**
     * A `Flow` that emits the last name every time it is updated.
     *
     * This will emit the new name each time it is set. When the name is unset, it will emit `null`.
     */
    val lastNameFlow: Flow<String?> = profileDataSource.lastNameFlow

    /**
     * Get the last name (if any).
     *
     * @return The last name that is currently set, or `null` if the last name is unset.
     */
    suspend fun getLastName(): String? = lastNameFlow.firstOrNull()

    /**
     * A `Flow` that emits the e-mail address every time it is updated.
     *
     * This will emit the new e-mail address each time it is set. When the e-mail address is unset,
     * it will emit `null`.
     */
    val emailFlow: Flow<String?> = profileDataSource.emailFlow

    /**
     * Get the e-mail address (if any).
     *
     * @return The e-mail address that is currently set, or `null` if the e-mail address is unset.
     */
    suspend fun getEmail(): String? = emailFlow.firstOrNull()

    suspend fun subscribe(): HttpResponse =
        httpClient.submitForm(
            url = url,
            formParameters = Parameters.build {
                append("email", getEmail().toString())
                append("firstName", getFirstName().toString())
                getLastName()?.let { last ->
                    append("lastName", last)
                }
            }
        )
}
