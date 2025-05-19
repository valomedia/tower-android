/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import media.valo.tower_android.TowerAndroidApplication
import media.valo.tower_android.data.local.preferences.profile.DummyProfileDataSource
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.newsletter.DummyNewsletterDataSource
import media.valo.tower_android.data.newsletter.NewsletterRepository
import media.valo.tower_android.ui.routes.loading.LoadingScreen

//
//  SignupForm.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//      * mvlexs
//

/**
 * A form for entering name and e-mail, shown to the user when first opening the app.
 *
 * @param modifier      Modifier for this element.
 * @param navController Used to navigate to the loading screen when the user submits the information.
 * @param viewModel     SignupFormViewModel dependency.
 */
@Composable
fun SignupForm(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SignupFormViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    var isLoading by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var isChecked by rememberSaveable { mutableStateOf(false) }
    val applicationContext = LocalContext.current.applicationContext as TowerAndroidApplication

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Angaben zu dir",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            "Verrate uns bitte deinen Namen, damit wir dich bei Anrufen besser ansprechen können.",
            modifier = Modifier.padding(8.dp)
        )
        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Vorname") },
                placeholder = { Text("Erforderlich") },
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .focusRequester(focusRequester)
            )
            Spacer(Modifier.width(6.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Nachname") },
                placeholder = { Text("Optional") },
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Optional, für Newsletter erforderlich") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        )
        if (viewModel.isEmailValid(email)) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.Center
                ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = !isChecked },
                    modifier = modifier.padding(8.dp),
                    enabled = true
                )
                Text(
                    text = "Ich möchte euren monatlichen Newsletter erhalten",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        Button(
            enabled = firstName.isNotBlank() && !isLoading,
            onClick = {
                scope.launch {
                    isLoading = true
                    viewModel.setFirstName(firstName)
                    viewModel.setLastName(lastName)
                    viewModel.setEmail(email)
                    navController.navigate(route = LoadingScreen)
                    if (isChecked) {
                        applicationContext.launch {
                            val x = viewModel.subscribeToNewsletter()
                            Log.d("Malik", x.toString())
                        }
                    }
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Anmelden")
        }
    }
}

/**
 * Preview for SignupForm.
 */
@Preview(showBackground = true, locale = "de-rDE")
@Composable
fun SignupFormPreview() {
    SignupForm(
        navController = rememberNavController(),
        viewModel = SignupFormViewModel(
            ProfileRepository(DummyProfileDataSource()),
            NewsletterRepository(DummyNewsletterDataSource())
        )
    )
}
