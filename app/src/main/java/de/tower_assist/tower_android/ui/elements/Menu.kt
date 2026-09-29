/******************************************************************************
 * Copyright (c) 2024-2026 valo.media GmbH                                    *
 * All rights reserved.                                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Affero General Public License as             *
 * published by the Free Software Foundation, either version 3 of the         *
 * License, or (at your option) any later version.                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Affero General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Affero General Public License   *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.elements

//
//  Menu.kt
//  Tower_Android
//

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import de.tower_assist.tower_android.data.local.preferences.PreferencesManager
import de.tower_assist.tower_android.data.local.preferences.credentials.CredentialRepository
import de.tower_assist.tower_android.data.local.preferences.credentials.DummyCredentialDataSource
import de.tower_assist.tower_android.data.local.preferences.profile.DummyProfileDataSource
import de.tower_assist.tower_android.data.local.preferences.profile.ProfileRepository
import de.tower_assist.tower_android.data.local.preferences.settings.DummySettingsDataSource
import de.tower_assist.tower_android.data.local.preferences.settings.SettingsRepository
import de.tower_assist.tower_android.ui.routes.home.HomeScreen
import de.tower_assist.tower_android.ui.routes.profile.ProfileScreen
import de.tower_assist.tower_android.ui.theme.TowerTheme

/**
 * The drawer containing the menu.
 *
 * @param modifier      `Modifier` for this element.
 * @param viewModel     `MenuViewModel` dependency.
 * @param navController Used to navigate to the various screens.
 * @param drawerState   State for the drawer.
 */
@Composable
fun Menu(
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = hiltViewModel(),
    navController: NavController,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    var userId by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.userIdFlow.collect { userId = it ?: "" }
        }
        scope.launch {
            viewModel.firstNameFlow.collect { firstName = it ?: "" }
        }
        scope.launch {
            viewModel.lastNameFlow.collect { lastName = it ?: "" }
        }
    }

    ModalDrawerSheet(
        modifier = modifier
    ) {
        ProfileCard(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider()
        Column(modifier = Modifier.padding(16.dp)) {
            NavigationDrawerItem(
                label = { Text(text = "Startseite") },
                selected = currentBackStackEntry?.destination?.hasRoute<HomeScreen>() == true,
                onClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(route = HomeScreen)
                    }
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "Benutzerprofil") },
                selected = currentBackStackEntry?.destination?.hasRoute<ProfileScreen>() == true,
                onClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(route = ProfileScreen)
                    }
                }
            )
        }
    }
}

/**
 * `Preview` for `Menu`.
 */
@Preview(showBackground = true, locale = "de-rDE")
@Composable
@SuppressLint("ViewModelConstructorInComposable")
fun MenuPreview() {
    val viewModel = MenuViewModel(
        preferencesManager = PreferencesManager(
            settingsRepository = SettingsRepository(DummySettingsDataSource()),
            credentialRepository = CredentialRepository(DummyCredentialDataSource()),
            profileRepository = ProfileRepository(DummyProfileDataSource())
        )
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()

    TowerTheme {
        Menu(
            viewModel = viewModel,
            drawerState = drawerState,
            navController = navController
        )
    }
}
