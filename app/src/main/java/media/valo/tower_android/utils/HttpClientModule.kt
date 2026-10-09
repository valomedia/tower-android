/******************************************************************************
 * Copyright (c) 2024-2026 valo.media GmbH                                    *
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

package media.valo.tower_android.utils

//
//  HttpClientModule.kt
//  Tower_Android
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
