/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.remote.tower

//
//  TowerDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * A data source representing the TOWER api.
 */
interface TowerDataSource {

    /**
     * Make a call to the index endpoint.
     *
     * This will make a call to the index endpoint, returning `Unit` when the call succeeds (meaning
     * the api is reachable and the credentials are valid), and throwing otherwise.
     */
    suspend fun index()

}
