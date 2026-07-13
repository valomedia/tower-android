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
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import media.valo.tower_android.R
import media.valo.tower_android.data.local.preferences.profile.DummyProfileDataSource
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.local.video.RandomVideoDataSource
import media.valo.tower_android.data.local.video.VideoRepository
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
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallScreen(
    viewModel: CallViewModel = hiltViewModel(),
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    modifier: Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val activity = LocalActivity.current
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    // Because for the way AnchoredDraggableState handles gestures, the menu will “fall” open when
    // returning from PiP. Since it should never be open when on this screen, we'll just hold it
    // shut as a workaround.
    LaunchedEffect(drawerState.isOpen) {
        runBlocking {
            drawerState.snapTo(DrawerValue.Closed)
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.sessionState == AssistanceSessionState.DISCONNECTED) {
            viewModel.startSession {
                viewModel.appScope.launch {
                    snackbarHostState.showSnackbar("Anruf fehlgeschlagen, bitte erneut versuchen.")
                }
            }
        }
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

    if (activity != null && activity.isInPictureInPictureMode) {
        PiPUi(
            viewModel = viewModel,
            activity = activity,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        NormalUi(
            viewModel = viewModel,
            scrollState = scrollState,
            activity = activity,
            modifier = modifier
        )
    }
}

@Composable
private fun NormalUi(
    viewModel: CallViewModel,
    scrollState: ScrollState,
    activity: Activity?,
    modifier: Modifier = Modifier
) {
    Row {
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                && viewModel.sessionState == AssistanceSessionState.CONNECTED) {
            Column(
                modifier = modifier.weight(3f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CameraFeed(
                    viewModel = viewModel,
                    activity = activity,
                    modifier = Modifier.weight(1f).padding(8.dp)
                )
            }
        }
        Column(
            modifier = modifier.verticalScroll(scrollState).weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE
                    && viewModel.sessionState == AssistanceSessionState.CONNECTED) {
                CameraFeed(
                    viewModel = viewModel,
                    activity = activity,
                    modifier = Modifier.weight(1f).padding(8.dp)
                )
            } else {
                Logo(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
            }
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
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun PiPUi(
    viewModel: CallViewModel,
    activity: Activity?,
    modifier: Modifier = Modifier
) {
    when (viewModel.sessionState) {
        // The assistant hung up while we were in PiP. Close the Activity.
        AssistanceSessionState.DISCONNECTED -> activity?.finish()

        // We're talking to the assistant. Show the camera feed.
        AssistanceSessionState.CONNECTED -> {
            CameraFeed(
                viewModel = viewModel,
                activity = activity,
                modifier = modifier
            )
        }

        // We're waiting, show the logo.
        else -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center, modifier = modifier
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun CameraFeed(
    viewModel: CallViewModel,
    activity: Activity?,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context -> FrameLayout(context) },
        update = { view ->
            viewModel.sessionState
            if (activity != null) {
                viewModel.enableVideoFrameSender(activity, view)
            }
        },
        modifier = modifier
    )
}

/**
 * Announce a change of the Assistance Sessions State to the user, using the accessibility manager.
 *
 * @param context               The application context.
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val appScope = CoroutineScopeModule().provideCoroutineScope()

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
                appScope = appScope,
                videoRepository = VideoRepository(RandomVideoDataSource(appScope))
            ),
            navController = rememberNavController(),
            snackbarHostState = remember { SnackbarHostState() },
            drawerState = drawerState,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        )
    }
}
