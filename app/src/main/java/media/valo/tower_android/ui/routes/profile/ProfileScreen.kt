/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.profile

//
//  ProfileScreen.kt
//  Tower_Android
//

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.serialization.Serializable
import media.valo.tower_android.data.local.preferences.profile.DummyProfileDataSource
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.model.Gender
import media.valo.tower_android.ui.elements.AppBarPreview

/**
 * Object for the navigation destination for the profile screen.
 */
@Serializable
object ProfileScreen

/**
 * The screen that allows the user to enter their profile information.
 *
 * @param modifier  `Modifier` for this element.
 * @param viewModel `ProfileViewModel` dependency.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    var isLoading by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var gender: Gender? by remember { mutableStateOf(null) }
    var birthdate: LocalDate? by remember { mutableStateOf(null) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getFirstName()?.let { firstName = it }
        viewModel.getLastName()?.let { lastName = it }
        viewModel.getGender()?.let { gender = it }
        viewModel.getBirthdate()?.let { birthdate = it }
        viewModel.getPhone()?.let { phone = it }
        viewModel.getEmail()?.let { email = it }
        isLoading = false
        focusRequester.requestFocus()
    }
    LaunchedEffect(firstName) { viewModel.setFirstName(firstName) }
    LaunchedEffect(lastName) { viewModel.setLastName(lastName) }
    LaunchedEffect(gender) { viewModel.setGender(gender) }
    LaunchedEffect(birthdate) { viewModel.setBirthdate(birthdate) }
    LaunchedEffect(phone) { viewModel.setPhone(phone) }
    LaunchedEffect(email) { viewModel.setEmail(email) }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 6.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Benutzerprofil",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Vorname") },
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
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
        EnumPicker(
            value = gender,
            onValueChange = { gender = it },
            label = { Text("Geschlecht") },
            enabled = !isLoading,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
        )
        DatePickerField(
            value = birthdate?.toJavaLocalDate(),
            onValueChange = { birthdate = it?.toKotlinLocalDate() },
            label = { Text("Geburtsdatum") },
            enabled = !isLoading,
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        )
    }
}

/**
 * `Preview` for `ProfileScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
@SuppressLint("ViewModelConstructorInComposable")
fun ProfileScreenPreview() {
    AppBarPreview { innerPadding ->
        ProfileScreen(
            viewModel = ProfileViewModel(ProfileRepository(DummyProfileDataSource())),
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        )
    }
}
