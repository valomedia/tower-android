/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.login

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val annotatedErrorText = buildAnnotatedString {
            append(
                "Bitte überprüfe ob du mit dem Internet verbunden bist, und die aktuelle Version der " +
                        "Tower-Fernassistenz-App installiert hast. Wenn das Problem weiterhin auftritt, " +
                        "versuche es später erneut, oder wende dich an unseren Support: "
            )
            pushStringAnnotation(tag = "EMAIL", annotation = "mailto:feedback@tower-assist.de")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
                append("feedback@tower-assist.de")
            }
            pop()
        }

        Text(
            "Verbindung fehlgeschlagen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        ClickableText(
            text = annotatedErrorText,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            onClick = { offset ->
                annotatedErrorText.getStringAnnotations("EMAIL", offset, offset)
                    .firstOrNull()?.let { annotation ->
                        when (annotation.tag) {
                            "EMAIL" -> context.startActivity(Intent(Intent.ACTION_SENDTO, annotation.item.toUri()))
                            }
                    }
            }
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
