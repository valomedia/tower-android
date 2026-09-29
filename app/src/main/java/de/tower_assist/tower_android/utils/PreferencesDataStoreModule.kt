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

package de.tower_assist.tower_android.utils

//
//  PreferencesDataStoreModule.kt
//  Tower_Android
//

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

private const val SETTINGS_DATA_STORE = "settings"
private const val CREDENTIALS_DATA_STORE = "credentials"
private const val PROFILE_DATA_STORE = "profile"

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CredentialsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProfileDataStore

@Module
@InstallIn(SingletonComponent::class)
class PreferencesDataStoreModule() {

    @SettingsDataStore
    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return createPreferenceDataStore(context, SETTINGS_DATA_STORE)
    }

    @CredentialsDataStore
    @Provides
    @Singleton
    fun provideCredentialsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return createPreferenceDataStore(context, CREDENTIALS_DATA_STORE)
    }

    @ProfileDataStore
    @Provides
    @Singleton
    fun provideProfileDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return createPreferenceDataStore(context, PROFILE_DATA_STORE)
    }

    private fun createPreferenceDataStore(context: Context, name: String): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            )
        ) {
            context.preferencesDataStoreFile(name)
        }
    }

}