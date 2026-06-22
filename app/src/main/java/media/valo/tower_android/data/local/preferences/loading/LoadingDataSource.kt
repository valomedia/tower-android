/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.loading

import kotlinx.coroutines.flow.Flow

//
//  LoadingDataSource.kt
//  Tower_Android
//
//  Created by:
//      * jan Hofherr
//

/**
 * A data source for information about the user.
 */
interface LoadingDataSource {

    /**
     * The version on the last login.
     *
     * This will be `null` when the app is started for the first time.
     */
    val lastLoginAppVersionFlow: Flow<String?>

    /**
     * Change the version on the last login.
     *
     * @param version  current version.
     */
    suspend fun setLastLoginAppVersion(version: String?)
}
