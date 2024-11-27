/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

//
//  HttpClientModule.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TowerHttpClient

@Module
@InstallIn(SingletonComponent::class)
class HttpClientModule() {

    @TowerHttpClient
    @Provides
    @Singleton
    fun provideTowerHttpClient(
        credentialRepository: CredentialRepository,
        @AppScope appScope: CoroutineScope
    ): HttpClient {
        val httpClient = HttpClient(OkHttp) {
            expectSuccess = true

            install(Logging) {
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
            install(Auth)
        }

        appScope.launch {
            credentialRepository.credentialFlow.filterNotNull().collect {
                httpClient.plugin(Auth).basic {
                    credentials {
                        BasicAuthCredentials(
                            username = credentialRepository.getUsername() ?: "",
                            password = credentialRepository.getPassword() ?: ""
                        )
                    }
                    sendWithoutRequest { true }
                }
            }
        }

        return httpClient
    }

}