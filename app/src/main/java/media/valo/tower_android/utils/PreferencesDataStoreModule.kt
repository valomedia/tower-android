/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

//
//  PreferencesDataStoreModule.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
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
private const val LOADING_DATA_STORE = "loading"

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CredentialsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProfileDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoadingDataStore

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

    @LoadingDataStore
    @Provides
    @Singleton
    fun provideLoadingDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return createPreferenceDataStore(context, LOADING_DATA_STORE)
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