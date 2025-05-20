/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
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
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TowerHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewsletterHttpClient

@Module
@InstallIn(SingletonComponent::class)
class HttpClientModule() {

    @TowerHttpClient
    @Provides
    @Singleton
    fun provideTowerHttpClient(
        credentialRepository: CredentialRepository,
        @AppScope appScope: CoroutineScope
    ): HttpClient = HttpClient(OkHttp) {
        expectSuccess = true
        install(Logging) {
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
        install(ContentNegotiation) {
            json(Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    /**
     * The HttpClient used for newsletter signup
     */
    @NewsletterHttpClient
    @Provides
    @Singleton
    fun provideNewsletterHttpClient(): HttpClient = HttpClient(OkHttp) {
        install(Logging) {
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }
}
