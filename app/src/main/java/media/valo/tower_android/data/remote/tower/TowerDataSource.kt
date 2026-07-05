/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.tower

import media.valo.tower_android.model.AwaitAssistanceResponse
import media.valo.tower_android.model.IndexResponse
import media.valo.tower_android.model.RegisterUserResponse
import media.valo.tower_android.model.RequestAssistanceResponse

//
//  TowerDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//      * mvlexs
//

/**
 * A data source representing the TOWER api.
 */
interface TowerDataSource {

    /**
     * Make a call to the index endpoint.
     *
     * This will make a call to the index endpoint, returning a indexResponse object, containing various
     * information about opening hours and the backend, when the call succeeds (meaning
     * the api is reachable and the credentials are valid), and throwing otherwise.
     */
    suspend fun index(): IndexResponse

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
    suspend fun registerUser(): RegisterUserResponse

    /**
     * Make a request for an assistance session.
     *
     * This will retrieve an access token for Azure Communication Services from the backend and add
     * the user to the queue of users waiting for an assistant.
     *
     * @return The `RequestAssistanceResponse` With the `UserToken`.
     */
    suspend fun requestAssistance(): RequestAssistanceResponse

    /**
     * Signal to the backend, that the caller is still waiting.
     *
     * This will inform the backend, that the caller is still on the line, so the assistance request
     * doesn't time out.
     *
     * @return The current position in the assistance queue.
     */
    suspend fun awaitAssistance(): AwaitAssistanceResponse

    /**
     * Signal to the backend, that the caller has given up on waiting.
     *
     * This will inform the backend, that the caller has cancelled the assistance request and no
     * assistant needs to respond anymore.
     */
    suspend fun cancelAssistance()

}
