/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

//
//  FakeSettingsDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import media.valo.tower_android.data.local.preferences.settings.SettingsDataSource

private const val API_ENDPOINT = "https://api.dev.tower-assist.de"

/**
 * A fake implementation of `SettingsDataSource`.
 *
 * This discards anything put into it and will emit static (but reasonable) values for all fields.
 */
class FakeSettingsDataSource : SettingsDataSource {

    override val apiEndpointFlow: Flow<String?> = flow { emit(API_ENDPOINT) }

    override suspend fun setApiEndpoint(apiEndpoint: String?) = Unit

}
