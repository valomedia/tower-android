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
//      * mvlexs
//

import android.Manifest
import android.app.PictureInPictureParams
import android.os.Bundle
import android.util.Rational
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import media.valo.tower_android.ui.elements.AppBar
import media.valo.tower_android.ui.elements.Menu
import media.valo.tower_android.ui.elements.RequirePermissions
import media.valo.tower_android.ui.routes.about.AboutScreen
import media.valo.tower_android.ui.routes.call.CallScreen
import media.valo.tower_android.ui.routes.call_history.CallHistoryScreen
import media.valo.tower_android.ui.routes.home.HomeScreen
import media.valo.tower_android.ui.routes.loading.LoadingScreen
import media.valo.tower_android.ui.routes.login.LoginScreen
import media.valo.tower_android.ui.routes.profile.ProfileScreen
import media.valo.tower_android.ui.routes.settings.SettingsScreen
import media.valo.tower_android.ui.theme.TowerTheme

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TowerTheme {
                val scrollBehavior =
                    TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val snackbarHostState = remember { SnackbarHostState() }
                navController = rememberNavController()

                DismissibleNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = { Menu(navController = navController, drawerState = drawerState) },
                    gesturesEnabled = false
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            AppBar(
                                scrollBehavior = scrollBehavior,
                                drawerState = drawerState,
                                navController = navController
                            )
                        }
                    ) { innerPadding ->
                        val modifier = Modifier
                            .padding(innerPadding)
                            .padding(8.dp)
                            .fillMaxSize()

                        NavHost(navController = navController, startDestination = LoadingScreen) {
                            composable<LoadingScreen> {
                                LoadingScreen(
                                    modifier = modifier,
                                    navController = navController
                                )
                            }
                            composable<LoginScreen> {
                                LoginScreen(
                                    modifier = modifier,
                                    navController = navController
                                )
                            }
                            composable<HomeScreen> {
                                RequirePermissions(
                                    permissions = listOf(
                                        Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.CAMERA
                                    ),
                                    rationale = "Um Dir helfen zu können, benötigen wir Deine Erlaubnis, auf "
                                            + "die Anruffunktionen deines Telefons zuzugreifen und Kamera und "
                                            + "Mikrofon einzuschalten.",
                                    modifier = modifier
                                ) {
                                    HomeScreen(
                                        navController = navController,
                                        modifier = modifier
                                    )
                                }
                            }
                            composable<CallScreen> {
                                CallScreen(
                                    navController = navController,
                                    snackbarHostState = snackbarHostState,
                                    modifier = modifier
                                )
                            }
                            composable<CallHistoryScreen> {
                                CallHistoryScreen(
                                    modifier = modifier
                                )
                            }
                            composable<ProfileScreen> {
                                ProfileScreen(
                                    modifier = modifier
                                )
                            }
                            composable<SettingsScreen> {
                                SettingsScreen(
                                    modifier = modifier
                                )
                            }
                            composable<AboutScreen> {
                                AboutScreen(
                                    modifier = modifier
                                )
                            }
                        }
                    }
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val navDestination = navController.currentDestination?.route.toString()
        val homeRoute = "media.valo.tower_android.ui.routes.home.HomeScreen"
        if (navDestination != homeRoute) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder().setAspectRatio(
                    Rational(9,16)
                ).build()
            )
        }
    }
}
