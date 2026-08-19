/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

//
//  FakeTowerDataSource.kt
//  Tower_Android
//

import media.valo.tower_android.data.remote.tower.TowerDataSource
import media.valo.tower_android.model.IndexResponse
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
 */
class FakeTowerDataSource(
    var indexResponse: IndexResponse? = dummyIndexResponse
) : TowerDataSource {

    override suspend fun index(): IndexResponse =
        indexResponse ?: throw IOException("The backend could not be reached.")

    override suspend fun registerUser() = dummyRegisterUserResponse

    override suspend fun requestAssistance() = dummyRequestAssistanceResponse

    override suspend fun awaitAssistance() = Unit

    override suspend fun cancelAssistance() = Unit

}
