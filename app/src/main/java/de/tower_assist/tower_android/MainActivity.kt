/******************************************************************************
 * Copyright (c) 2024-2026.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android

//
//  MainActivity.kt
//  Tower_Android
//

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Rational
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import de.tower_assist.tower_android.ui.TowerApp
import de.tower_assist.tower_android.ui.routes.call.CallScreen
import de.tower_assist.tower_android.ui.theme.TowerTheme

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()

            TowerTheme {
                TowerApp(navController = navController)
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (navController.currentDestination?.hasRoute<CallScreen>() == true
                && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder().setAspectRatio(
                    Rational(9, 16)
                ).build()
            )
        }
    }

}
