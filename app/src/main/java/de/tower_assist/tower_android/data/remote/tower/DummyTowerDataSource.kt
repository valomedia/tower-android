/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
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
