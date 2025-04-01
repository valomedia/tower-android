/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.call

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.FrameLayout
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import media.valo.tower_android.data.local.preferences.profile.DummyProfileDataSource
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.remote.tower.DummyTowerDataSource
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.model.AssistanceSessionState
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.ui.elements.Logo
import media.valo.tower_android.ui.routes.home.HomeScreen
import media.valo.tower_android.utils.CoroutineScopeModule
import media.valo.tower_android.utils.JsonModule

//
//  CallScreen.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//      * mvlexs
//

private const val REQUEST_CHECK_SETTINGS = 1

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
@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Composable
fun CallScreen(
    viewModel: CallViewModel = hiltViewModel(),
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val activity = LocalActivity.current
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        if (viewModel.sessionState == AssistanceSessionState.DISCONNECTED) {
            viewModel.startSession {
                viewModel.appScope.launch {
                    snackbarHostState.showSnackbar("Anruf fehlgeschlagen, bitte erneut versuchen.")
                }
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) = viewModel.putCallInBackground(context)
            override fun onResume(owner: LifecycleOwner) = viewModel.putCallInForeground(context)
        })
    }

    LaunchedEffect(viewModel.sessionState) {
        if (viewModel.sessionState == AssistanceSessionState.DISCONNECTED) {
            navController.navigate(route = HomeScreen) { popUpTo(navController.graph.id) }
        }
        announceStateChange(context, accessibilityManager, viewModel.sessionState.toString())
    }

    LaunchedEffect(
        viewModel.isRequestingLocationUpdates,
        locationPermissionState.revokedPermissions
    ) {
        if (viewModel.isRequestingLocationUpdates) {
            if (!locationPermissionState.allPermissionsGranted) {
                locationPermissionState.launchMultiplePermissionRequest()
            } else {
                viewModel.startLocationUpdates { exception ->
                    // Location access is granted, but other settings prevent the location from
                    // being obtained.
                    try {
                        // Prompt the user to change the settings.
                        exception.startResolutionForResult(activity!!, REQUEST_CHECK_SETTINGS)
                    } catch (_: Exception) {
                    }
                }
            }
        }
    }

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        if (viewModel.sessionState == AssistanceSessionState.CONNECTED) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = modifier
                        .verticalScroll(scrollState)
                        .weight(3f)
                        .fillMaxHeight()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CameraFeed(viewModel, activity, viewModel.isInBackground)
                }
                Column(
                    modifier = modifier
                        .verticalScroll(scrollState)
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Logo(modifier = Modifier
                        .weight(1f)
                        .padding(8.dp))
                    GeneralUi(viewModel)
                }
            }
        } else {
            Column(
                modifier = modifier.verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Logo(modifier = Modifier
                    .weight(1f)
                    .padding(8.dp))
                GeneralUi(viewModel)
            }
        }
    } else {
        Column(
            modifier = modifier.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.sessionState == AssistanceSessionState.CONNECTED) {
                CameraFeed(viewModel, activity, viewModel.isInBackground)
            } else {
                Logo(modifier = Modifier
                    .weight(1f)
                    .padding(8.dp))
            }
            GeneralUi(viewModel)
        }
    }

}

@Composable
private fun GeneralUi(viewModel: CallViewModel) {
    Text(
        viewModel.sessionState.toString(),
        modifier = Modifier
            .padding(8.dp)
            .semantics { invisibleToUser() })
    ExtendedFloatingActionButton(
        onClick = { viewModel.endSession() },
        icon = { Icon(Icons.Filled.Phone, "Auflegen") },
        text = { Text(text = "Auflegen") },
        containerColor = Color.Red,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
private fun ColumnScope.CameraFeed(viewModel: CallViewModel, activity: Activity?, isInBackground: Boolean) {
    if (!isInBackground) {
        AndroidView(
            factory = { context -> FrameLayout(context) },
            update = { view ->
                viewModel.sessionState
                if (activity != null) {
                    viewModel.showPreview(activity, view)
                }
            },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
        )
    }
}

/**
 * Announce a change of the Assistance Sessions State to the user, using the accessibility manager.
 *
 * @param context               The applications context.
 * @param accessibilityManager  The accessibility manager.
 * @param message               The State change to announce.
 */
private fun announceStateChange(
    context: Context,
    accessibilityManager: AccessibilityManager?,
    message: String
) {
    accessibilityManager?.let {
        if (it.isEnabled) {
            it.sendAccessibilityEvent(AccessibilityEvent().apply {
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
                profileRepository = ProfileRepository(DummyProfileDataSource()),
                json = JsonModule().provideJson(),
                context = LocalContext.current,
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current),
                locationServicesSettingsClient = LocationServices.getSettingsClient(LocalContext.current),
                looper = Looper.getMainLooper(),
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
