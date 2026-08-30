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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import media.valo.tower_android.ui.routes.about.AboutScreen
import media.valo.tower_android.ui.routes.call.CallScreen
import media.valo.tower_android.ui.routes.closed.ClosedScreen
import media.valo.tower_android.ui.routes.contact.ContactScreen
import media.valo.tower_android.ui.routes.loading.LoadingScreen
import media.valo.tower_android.ui.routes.login.LoginScreen
import media.valo.tower_android.ui.routes.news.NewsScreen
import media.valo.tower_android.ui.routes.outdated.OutdatedAppVersionScreen
import media.valo.tower_android.ui.routes.settings.SettingsScreen
import media.valo.tower_android.ui.theme.TowerTheme

/**
 * The destinations that show no navigation icon.
 *
 * These are destinations the user must not be able to navigate away from: the loading screen,
 * the call screen, and the gates the rest of the app sits behind.
 */
internal val destinationsWithoutNavigationIcon = listOf(
    LoadingScreen::class,
    LoginScreen::class,
    CallScreen::class,
    ClosedScreen::class,
    OutdatedAppVersionScreen::class
)

/**
 * The destinations that show a back button rather than the drawer.
 *
 * These are reached from the overflow menu rather than from the drawer, so the drawer would offer
 * no way back to where the user came from.
 */
internal val destinationsWithBackButton = listOf(
    SettingsScreen::class,
    AboutScreen::class,
    ContactScreen::class,
    NewsScreen::class
)

/**
 * The bar at the top of the app containing the buttons for back, menu and more.
 *
 * @param modifier          `Modifier` for this element.
 * @param scrollBehavior    How the bar should behave when the content under it is scrolled.
 * @param drawerState       State of the drawer containing the menu.
 * @param navController     Used to navigate to the various screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
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

    if (activity?.isInPictureInPictureMode != true) {
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
                val destination = currentDestination
                when {
                    destination == null || destinationsWithoutNavigationIcon.any {
                        destination.hasRoute(it)
                    } -> Unit
                    destinationsWithBackButton.any { destination.hasRoute(it) } -> IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                    else -> IconButton(
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
                            text = { Text("Kontakt") },
                            onClick = {
                                navController.navigate(route = ContactScreen)
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
                        DropdownMenuItem(
                            text = { Text("Neuigkeiten") },
                            onClick = {
                                navController.navigate(route = NewsScreen)
                                expanded = false
                            }
                        )
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
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()

    TowerTheme {
        Scaffold(
            topBar = {
                AppBar(
                    scrollBehavior = scrollBehavior,
                    drawerState = drawerState,
                    navController = navController
                )
            },
            content = content
        )
    }
}
