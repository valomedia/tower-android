/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.data.local.preferences.loading

//
//  LoadingRepository.kt
//  Tower_Android
//
//  Created by:
//      * Jan Hofherr
//

import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * Repository for information about the version.
 *
 * @param loadingDataSource `LoadingDataSource` dependency.
 */
class LoadingRepository @Inject constructor(
    private val loadingDataSource: LoadingDataSource
) {

    /**
     * Get the version on the last login (if any).
     *
     * @return The version on the last login , or `null` if it is unset (first login).
     */
    suspend fun getLastLoginAppVersion(): String? = loadingDataSource.lastLoginAppVersionFlow.firstOrNull()

    /**
     * Change the version on the last login.
     *
     * @param Current version.
     */
    suspend fun setLastLoginAppVersion(version: String?) = loadingDataSource.setLastLoginAppVersion(version)
}
