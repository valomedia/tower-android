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

package de.tower_assist.tower_android.data.remote.tower

import de.tower_assist.tower_android.model.dummyAwaitAssistanceResponse
import de.tower_assist.tower_android.model.dummyIndexResponse
import de.tower_assist.tower_android.model.dummyRegisterUserResponse
import de.tower_assist.tower_android.model.dummyRequestAssistanceResponse

//
//  DummyTowerDataSource.kt
//  Tower_Android
//

/**
 * A dummy implementation of `TowerDataSource`.
 *
 * This implements all api-calls as no-ops.
 */
class DummyTowerDataSource : TowerDataSource {

    override suspend fun index() = dummyIndexResponse

    override suspend fun registerUser() = dummyRegisterUserResponse

    override suspend fun requestAssistance() = dummyRequestAssistanceResponse

    override suspend fun awaitAssistance() = dummyAwaitAssistanceResponse

    override suspend fun cancelAssistance() = Unit

}
