/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.elements

//
//  AppBar.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.credentials.DummyCredentialDataSource
import media.valo.tower_android.data.local.preferences.profile.DummyProfileDataSource
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.ui.routes.about.AboutScreen
import media.valo.tower_android.ui.routes.call.CallScreen
import media.valo.tower_android.ui.routes.loading.LoadingScreen
import media.valo.tower_android.ui.routes.login.LoginScreen
import media.valo.tower_android.ui.routes.settings.SettingsScreen
import media.valo.tower_android.ui.theme.TowerTheme

/**
 * The bar at the top of the app containing the buttons for back, menu and more.
 *
 * @param modifier          `Modifier` for this element.
 * @param viewModel         `AppBarViewModel` dependency.
 * @param scrollBehavior    How the bar should behave when the content under it is scrolled.
 * @param drawerState       State of the drawer containing the menu.
 * @param navController     Used to navigate to the various screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    viewModel: AppBarViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior,
    drawerState: DrawerState,
    navController: NavController
) {
    val scope = rememberCoroutineScope()

    val activity = LocalActivity.current

    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination by remember {
        derivedStateOf { currentBackStackEntry?.destination }
    }

    var expanded by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.userIdFlow.collect { userId ->
            isLoggedIn = !userId.isNullOrBlank()
        }
    }

    if (activity == null || !activity.isInPictureInPictureMode) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "Tower Fernassistenz",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (currentDestination != null
                        && currentDestination?.hasRoute<LoginScreen>() != true
                        && currentDestination?.hasRoute<LoadingScreen>() != true
                        && currentDestination?.hasRoute<CallScreen>() != true
                    ) {
                        if (currentDestination?.hasRoute<SettingsScreen>() != true
                            && currentDestination?.hasRoute<AboutScreen>() != true
                        ) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) {
                                                open()
                                            } else {
                                                close()
                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Menü"
                                )
                            }
                        } else {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Zurück"
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (currentDestination != null
                        && currentDestination?.hasRoute<CallScreen>() != true
                    ) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Mehr"
                            )
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Einstellungen") },
                                onClick = {
                                    navController.navigate(route = SettingsScreen)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Über") },
                                onClick = {
                                    navController.navigate(route = AboutScreen)
                                    expanded = false
                                }
                            )
                            if (isLoggedIn) {
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Abmelden") },
                                    onClick = {
                                        scope.launch {
                                            viewModel.logout()
                                            expanded = false
                                            drawerState.close()
                                            navController.navigate(route = LoginScreen) {
                                                popUpTo(navController.graph.id)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                modifier = modifier
            )
    }
}

/**
 * `Preview` for `AppBar`.
 *
 * @param content   The content to show under the bar, defaults to nothing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(locale = "de-rDE")
@Composable
fun AppBarPreview(content: @Composable ((PaddingValues) -> Unit) = {}) {
    val viewModel = AppBarViewModel(
        CredentialRepository(DummyCredentialDataSource()),
        ProfileRepository(DummyProfileDataSource())
    )
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()

    TowerTheme {
        Scaffold(
            topBar = {
                AppBar(
                    viewModel = viewModel,
                    scrollBehavior = scrollBehavior,
                    drawerState = drawerState,
                    navController = navController
                )
            },
            content = content
        )
    }
}
