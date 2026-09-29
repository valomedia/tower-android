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

package de.tower_assist.tower_android.data.local.preferences.settings

//
//  DummySettingsDataSource.kt
//  Tower_Android
//

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

    override val lastSeenNewsVersionFlow: Flow<String?> = flow { emit(null) }

    override suspend fun setLastSeenNewsVersion(version: String?) = Unit
}
