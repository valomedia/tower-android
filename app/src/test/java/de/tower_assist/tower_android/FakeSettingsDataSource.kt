/******************************************************************************
 * Copyright (c) 2024-2026.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android

//
//  FakeSettingsDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import de.tower_assist.tower_android.data.local.preferences.settings.SettingsDataSource

private const val API_ENDPOINT = "https://api.dev.tower-assist.de"

/**
 * A fake implementation of `SettingsDataSource`.
 *
 * This starts out with static (but reasonable) values and keeps anything put into it in memory.
 * Use `DummySettingsDataSource` where writes should be discarded instead.
 */
class FakeSettingsDataSource : SettingsDataSource {

    private val apiEndpoint = MutableStateFlow<String?>(API_ENDPOINT)

    override val apiEndpointFlow: Flow<String?> = apiEndpoint

    override suspend fun setApiEndpoint(apiEndpoint: String?) {
        this.apiEndpoint.value = apiEndpoint
    }

    private val lastSeenNewsVersion = MutableStateFlow<String?>(null)

    override val lastSeenNewsVersionFlow: Flow<String?> = lastSeenNewsVersion

    override suspend fun setLastSeenNewsVersion(version: String?) {
        this.lastSeenNewsVersion.value = version
    }

}
