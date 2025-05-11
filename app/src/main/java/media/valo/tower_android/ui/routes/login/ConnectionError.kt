/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.login

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import media.valo.tower_android.ui.routes.loading.LoadingScreen

//
//  ConnectionError.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

/**
 * A message telling the user that the app could not connect to the service, with a button to retry.
 *
 * @param modifier      Modifier for this element.
 * @param navController User to navigate to the loading screen when the user chooses to retry.
 */
@Composable
fun ConnectionError(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val mailIntent = Intent(Intent.ACTION_VIEW,("mailto:" + "feedback@tower-assist.de").toUri())

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Verbindung fehlgeschlagen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            """
                Bitte überprüfe ob du mit dem Internet verbunden bist, und die aktuelle Version der
                Tower-Fernassistenz-App installiert hast. Wenn das Problem weiterhin auftritt,
                versuche es später erneut, oder wende dich an unseren Support:
            """.trimIndent().replace("\n", " "),
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "feedback@tower-assist.de",
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable(onClick = {
                    context.startActivity(mailIntent)
                })
                .padding(16.dp)
        )
        Button(
            onClick = {
                scope.launch {
                    navController.navigate(route = LoadingScreen)
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Erneut versuchen")
        }
    }

}

/**
 * Preview for ConnectionError.
 */
@Preview(showBackground = true, locale = "de-rDE")
@Composable
fun ConnectionErrorPreview() {
    ConnectionError(navController = rememberNavController())
}
