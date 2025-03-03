/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.tower

import media.valo.tower_android.model.dummyRegisterUserResponse
import media.valo.tower_android.model.dummyRequestAssistanceResponse

//
//  DummyTowerDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * A dummy implementation of `TowerDataSource`.
 *
 * This implements all api-calls as no-ops.
 */
class DummyTowerDataSource : TowerDataSource {

    override suspend fun index() = Unit

    override suspend fun registerUser() = dummyRegisterUserResponse

    override suspend fun requestAssistance() = dummyRequestAssistanceResponse

    override suspend fun awaitAssistance() = Unit

    override suspend fun cancelAssistance() = Unit

}
