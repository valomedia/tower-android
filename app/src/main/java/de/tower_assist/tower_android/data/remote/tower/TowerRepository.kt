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

//
//  TowerRepository.kt
//  Tower_Android
//

import javax.inject.Inject

/**
 * A repository for the TOWER api.
 *
 * @param towerDataSource   `TowerDataSource` dependency.
 */
class TowerRepository @Inject constructor(
    private val towerDataSource: TowerDataSource
) {

    /**
     * Make a call to the index endpoint.
     *
     * This will make a call to the index endpoint, returning a indexResponse object, containing various
     * information about opening hours and the backend, when the call succeeds (meaning
     * the api is reachable and the credentials are valid), and throwing otherwise.
     */
    suspend fun index() = towerDataSource.index()

    /**
     * Make a request to create an identity for this instance of the app.
     *
     * This will create an identity on the tower backend under a random UUID, along with an
     * associated identity in Azure Communication Services. The UUID of this lightweight user will
     * be supplied with all following requests to allow the backend to associate all requests coming
     * from the same instance of the app.
     *
     * @return The `RegisterUserResponse` with the UUID.
     */
    suspend fun registerUser() = towerDataSource.registerUser()

    /**
     * Make a request for an assistance session.
     *
     * This will retrieve an access token for Azure Communication Services from the backend and add
     * the user to the queue of users waiting for an assistant.
     *
     * @return The `RequestAssistanceResponse` With the `UserToken`.
     */
    suspend fun requestAssistance() = towerDataSource.requestAssistance()

    /**
     * Signal to the backend, that the caller is still waiting.
     *
     * This will inform the backend, that the caller is still on the line, so the assistance request
     * doesn't time out.
     *
     * @return The current position in the assistance queue.
     */
    suspend fun awaitAssistance() = towerDataSource.awaitAssistance()

    /**
     * Signal to the backend, that the caller has given up on waiting.
     *
     * This will inform the backend, that the caller has cancelled the assistance request and no
     * assistant needs to respond anymore.
     */
    suspend fun cancelAssistance() = towerDataSource.cancelAssistance()

}
