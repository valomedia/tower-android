/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltAndroidApp
class TowerAndroidApplication : Application() {

    /**
     * `Context` dependency.
     *
     * This is needed as a workaround for https://github.com/google/dagger/issues/3601.
     */
    @Inject
    @ApplicationContext
    lateinit var context: Context

}
