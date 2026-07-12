/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A dummy implementation of `SettingsDataSource`.
 *
 * This discards anything put into it and will always emit `null` for all fields.
 */
class DummySettingsDataSource : SettingsDataSource {

    override val apiEndpointFlow: Flow<String?> = flow { emit("") }

    override suspend fun setApiEndpoint(apiEndpoint: String?) = Unit

}
