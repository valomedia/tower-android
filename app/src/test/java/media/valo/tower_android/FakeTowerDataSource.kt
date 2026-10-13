/******************************************************************************
 * Copyright (c) 2026 valo.media GmbH                                         *
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
//  FakeTowerDataSource.kt
//  Tower_Android
//

import media.valo.tower_android.data.remote.tower.TowerDataSource
import media.valo.tower_android.model.AwaitAssistanceResponse
import media.valo.tower_android.model.IndexResponse
import media.valo.tower_android.model.dummyAwaitAssistanceResponse
import media.valo.tower_android.model.dummyIndexResponse
import media.valo.tower_android.model.dummyRegisterUserResponse
import media.valo.tower_android.model.dummyRequestAssistanceResponse
import java.io.IOException

/**
 * A fake implementation of `TowerDataSource`.
 *
 * This answers all api-calls from memory, so that tests can put the backend into a given state.
 *
 * @param indexResponse The `IndexResponse` to answer index() with, or `null` to make index() fail
 *                      the way an unreachable backend would.
 * @param awaitAssistanceResponse The `AwaitAssistanceResponse` to answer awaitAssistance() with.
 */
class FakeTowerDataSource(
    var indexResponse: IndexResponse? = dummyIndexResponse,
    var awaitAssistanceResponse: AwaitAssistanceResponse = dummyAwaitAssistanceResponse
) : TowerDataSource {

    override suspend fun index(): IndexResponse =
        indexResponse ?: throw IOException("The backend could not be reached.")

    override suspend fun registerUser() = dummyRegisterUserResponse

    override suspend fun requestAssistance() = dummyRequestAssistanceResponse

    override suspend fun awaitAssistance() = awaitAssistanceResponse

    override suspend fun cancelAssistance() = Unit

}
