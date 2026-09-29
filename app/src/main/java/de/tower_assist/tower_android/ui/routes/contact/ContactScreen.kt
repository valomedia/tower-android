/******************************************************************************
 * Copyright (c) 2025-2026.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.contact

//
//  AboutScreen.kt
//  Tower_Android
//

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

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
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState).padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = "Kontakt",
            modifier = Modifier.padding(8.dp)
        )
        Column(
            horizontalAlignment = AbsoluteAlignment.Left
        ){
            Text(
                text = "Wir freuen uns über deine Fragen und Anregungen. " +
                        "Schreib uns jederzeit eine Mail oder ruf uns an. " +
                        "Wir sind von Montag bis Freitag zwischen 9 und 17 Uhr erreichbar. " +
                        "Weitere Infos findest du auf unserer Webseite.",
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = buildAnnotatedString {
                    append("Mail: ")
                    withLink(
                        LinkAnnotation.Url(
                            "mailto:feedback@tower-assist.de",
                            TextLinkStyles(
                                style = SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        )
                    ) { append("feedback@tower-assist.de") }
                },
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = buildAnnotatedString {
                    append("Telefon: ")
                    withLink(
                        LinkAnnotation.Url(
                            "tel:+491738406203",
                            TextLinkStyles(
                                style = SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        )
                    ) { append("+49 173 8406203") }
                },
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = buildAnnotatedString {
                    append("Web: ")
                    withLink(
                        LinkAnnotation.Url(
                            "https://tower-assist.de/",
                            TextLinkStyles(
                                style = SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        )
                    ) { append("tower-assist.de") }
                },
                modifier = Modifier.padding(8.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text(
                text = "Tower Assist ist ein Angebot von:",
                modifier = Modifier.padding(8.dp)
            )
            val mapsUrl =
                "https://maps.google.com/?q=Bathildisheim%20e.V.%20Bathildisstraße%207,%2034454%20Bad%20Arolsen"
            Text(
                text = buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Url(
                            mapsUrl,
                            TextLinkStyles(
                                style = SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        )
                    ) {
                        append("Bathildisheim e.V.\n")
                        append("Bathildisstraße 7\n")
                        append("34454 Bad Arolsen")
                    }
                },
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = buildAnnotatedString {
                    append("Quellcode: ")
                    withLink(
                        LinkAnnotation.Url(
                            "https://github.com/valomedia/tower-android/",
                            TextLinkStyles(
                                style = SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        )
                    ) { append("github.com/valomedia/tower-android") }
                },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
