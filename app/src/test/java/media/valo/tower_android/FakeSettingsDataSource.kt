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

package media.valo.tower_android

//
//  FakeSettingsDataSource.kt
//  Tower_Android
//

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import media.valo.tower_android.data.local.preferences.settings.SettingsDataSource

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
