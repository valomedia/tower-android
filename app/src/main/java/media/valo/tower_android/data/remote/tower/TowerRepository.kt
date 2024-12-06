/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.tower

//
//  TowerRepository.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
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
     * This will make a call to the index endpoint, returning `Unit` when the call succeeds (meaning
     * the api is reachable and the credentials are valid), and throwing otherwise.
     */
    suspend fun index() = towerDataSource.index()

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
     */
    suspend fun awaitAssistance() = towerDataSource.awaitAssistance()

}
