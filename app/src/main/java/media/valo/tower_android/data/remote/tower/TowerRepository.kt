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

}
