/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.loading

//
//  DummyLoadingDataSource.kt
//  Tower_Android
//
//  Created by:
//      * Jan Hofherr
//

/**
 * A dummy implementation of `LoadingDataSource`.
 *
 * This discards anything put into it and will always have the value `null` for all fields.
 */
class DummyLoadingDataSource : LoadingDataSource {

    override val lastLoginAppVersion: String? = null

    override suspend fun setLastLoginAppVersion(version: String?) = Unit

}
