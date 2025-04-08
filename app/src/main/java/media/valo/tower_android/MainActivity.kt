/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android

//
//  MainActivity.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import android.app.PictureInPictureParams
import android.os.Bundle
import android.util.Rational
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import media.valo.tower_android.ui.TowerApp
import media.valo.tower_android.ui.theme.TowerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TowerTheme {
                TowerApp()
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPictureInPictureMode(
            PictureInPictureParams.Builder().setAspectRatio(
                Rational(9,16)
            ).build()
        )
    }
}
