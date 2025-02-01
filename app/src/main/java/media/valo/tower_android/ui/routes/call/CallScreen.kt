/******************************************************************************
 * Copyright (c) 2024-2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.call

import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import media.valo.tower_android.data.remote.tower.DummyTowerDataSource
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.model.AssistanceSessionState
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.ui.routes.home.HomeScreen
import media.valo.tower_android.utils.CoroutineScopeModule

//
//  CallScreen.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//      * mvlexs
//

/**
 * Object for the navigation destination for the call screen.
 */
@Serializable
object CallScreen

/**
 * Screen shown while in a call, or while waiting for the assistant to connect.
 *
 * @param viewModel         `CallViewModel` dependency.
 * @param navController     Used to navigate back to the home screen once the call ends.
 * @param snackbarHostState Used to show a snackbar if the call fails.
 */
@Composable
fun CallScreen(
    viewModel: CallViewModel = hiltViewModel(),
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
    var isFirstAnnouncement = true

    LaunchedEffect(Unit) {
        viewModel.startSession(context, onCallError = {
            viewModel.appScope.launch {
                snackbarHostState.showSnackbar("Anruf fehlgeschlagen, bitte erneut versuchen.")
            }
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) = viewModel.endSession()
        })
    }

    LaunchedEffect(viewModel.sessionState) {
        if (viewModel.sessionState == AssistanceSessionState.DISCONNECTED) {
            navController.navigate(route = HomeScreen) { popUpTo(navController.graph.id) }
        }
        if (!isFirstAnnouncement) {
            announceStateChange(context, accessibilityManager, viewModel.sessionState.toString())
        }
        isFirstAnnouncement = false
    }

    CameraViewFinder(
        modifier = Modifier.fillMaxSize()
    )
    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(viewModel.sessionState.toString(), modifier = Modifier.padding(8.dp))
        ExtendedFloatingActionButton(
            onClick = { viewModel.endSession() },
            icon = { Icon(Icons.Filled.Phone, "Auflegen") },
            text = { Text(text = "Auflegen") },
            containerColor = Color.Red,
            modifier = Modifier.padding(8.dp)
        )
    }
}

/**
 * Announce a change of the Assistance Sessions State to the user, using the accessibility manager.
 *
 * @param context               The application context.
 * @param accessibilityManager  The accessibility manager.
 * @param message               The State change to announce.
 */
private fun announceStateChange(context: Context, accessibilityManager: AccessibilityManager?, message: String) {
    accessibilityManager?.let {
        if (it.isEnabled) {
            it.sendAccessibilityEvent(AccessibilityEvent.obtain().apply {
                eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
                className = context.javaClass.name
                packageName = context.packageName
                text.add(message)
            })
        }
    }
}

/**
 * `Preview` for `CallScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
fun CallScreenPreview() {
    AppBarPreview { innerPadding ->
        CallScreen(
            viewModel = CallViewModel(
                towerRepository = TowerRepository(DummyTowerDataSource()),
                appScope = CoroutineScopeModule().provideCoroutineScope()
            ),
            navController = rememberNavController(),
            snackbarHostState = remember { SnackbarHostState() },
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        )
    }
}
