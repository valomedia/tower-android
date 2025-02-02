/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/


package media.valo.tower_android.ui.routes.call

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

//
//  CameraViewFinder.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

/**
 * Composable that gets the camera ready to be displayed.
 *
 * @param modifier The `Modifier` for this element, determining its size and position.
 **/
@Composable
fun CameraViewFinder(
    modifier: Modifier = Modifier
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            val cameraController = LifecycleCameraController(context)
            cameraController.bindToLifecycle(lifecycleOwner)
            previewView.controller = cameraController
            previewView
        },
        modifier = modifier
    )
}
