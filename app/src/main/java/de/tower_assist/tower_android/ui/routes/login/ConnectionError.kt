/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.login

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import de.tower_assist.tower_android.ui.routes.loading.LoadingScreen

//
//  ConnectionError.kt
//  Tower_Android
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
            text = buildAnnotatedString {
                append(
                    "Bitte überprüfe ob du mit dem Internet verbunden bist, und die aktuelle Version der " +
                            "Tower-Fernassistenz-App installiert hast. Wenn das Problem weiterhin auftritt, " +
                            "versuche es später erneut, oder wende dich an unseren Support: "
                )
                withLink(
                    LinkAnnotation.Url(
                        "mailto:feedback@tower-assist.de",
                        TextLinkStyles(style = SpanStyle(color = Color.Blue))
                    )
                ) {
                    append("feedback@tower-assist.de")
                }
            },
            modifier = Modifier.padding(8.dp)
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
