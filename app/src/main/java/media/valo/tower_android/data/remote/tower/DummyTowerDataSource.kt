/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.tower

import media.valo.tower_android.model.dummyIndexResponse
import media.valo.tower_android.model.dummyRegisterUserResponse
import media.valo.tower_android.model.dummyRequestAssistanceResponse

/**
 * A dummy implementation of `TowerDataSource`.
 *
 * This implements all api-calls as no-ops.
 */
class DummyTowerDataSource : TowerDataSource {

    override suspend fun index() = dummyIndexResponse

    override suspend fun registerUser() = dummyRegisterUserResponse

    override suspend fun requestAssistance() = dummyRequestAssistanceResponse

    override suspend fun awaitAssistance() = Unit

    override suspend fun cancelAssistance() = Unit

}
