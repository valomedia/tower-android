/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.contact

//
//  AboutScreen.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import androidx.core.net.toUri

/**
 * Object for the navigation destination for the contact screen.
 */
@Serializable
object ContactScreen

/**
 * Screen with detailed information about the Tower service and possibilities to contact the support.
 *
 * @param modifier  `Modifier` for this element.
 */
@Composable
fun ContactScreen(
    modifier: Modifier = Modifier,
){

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val contactScreenText = buildAnnotatedString {
            append("Wir freuen uns über deine Fragen und Anregungen. Schreib uns jederzeit eine Mail an:\n")
            pushStringAnnotation(tag = "EMAIL", annotation = "mailto:feedback@tower-assist.de")
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.primary)) {
                append("feedback@tower-assist.de")
            }
            pop()
            append("\n")
            append("Du kannst uns auch anrufen unter der Nummer:\n")
            pushStringAnnotation(tag = "PHONE", annotation = "tel:+491738406203")
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.primary)) {
                append("+49 173 8406203")
            }
            pop()
            append("\n")
            append("Wir sind von Montag bis Freitag zwischen 9 und 17 Uhr erreichbar.\n" +
                    "Weitere Infos findest du auf unserer Webseite unter:\n")
            pushStringAnnotation(tag = "URL", annotation = "https://tower-assist.de/")
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.primary)) {
                append("https://tower-assist.de/")
            }
            pop()
        }
        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = "Kontakt",
                modifier = Modifier.padding(20.dp)
            )
            ClickableText(
                text = contactScreenText,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                modifier = Modifier.padding(16.dp),
                onClick = { offset ->
                    contactScreenText.getStringAnnotations(start = offset, end = offset)
                        .firstOrNull()?.let { annotation ->
                            when (annotation.tag) {
                                "EMAIL" -> context.startActivity(Intent(Intent.ACTION_SENDTO,
                                    annotation.item.toUri()))
                                "PHONE" -> context.startActivity(Intent(Intent.ACTION_DIAL,
                                    annotation.item.toUri()))
                                "URL" -> context.startActivity(Intent(Intent.ACTION_VIEW,
                                    annotation.item.toUri()))
                            }
                        }

                }
            )
        }
        Text(
            text =
                    "Tower Fernassistanz ist ein Angebot von:\n"+
                    "Bathildisheim e.V.\n" +
                    "Bathildisstraße 7\n" +
                    "34454 Bad Arolsen",
            modifier = Modifier.weight(1f)
        )
    }
}