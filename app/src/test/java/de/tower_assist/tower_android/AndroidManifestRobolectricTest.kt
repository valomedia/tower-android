/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  AndroidManifestRobolectricTest.kt
//  Tower_Android
//

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class AndroidManifestRobolectricTest {

    @Test
    fun `loads application resources and manifest metadata`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_ACTIVITIES or PackageManager.GET_PERMISSIONS
        )

        assertEquals("Tower Assist", context.getString(R.string.app_name))
        assertEquals(R.string.app_name, packageInfo.applicationInfo?.labelRes)
        assertTrue(
            packageInfo.activities.orEmpty().any { activityInfo ->
                activityInfo.name == MainActivity::class.java.name && activityInfo.exported
            }
        )
        assertTrue(
            packageInfo.requestedPermissions.orEmpty().contains(android.Manifest.permission.CAMERA)
        )
    }

}
