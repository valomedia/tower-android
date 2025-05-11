package media.valo.tower_android.ui.routes.contact

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import androidx.core.net.toUri

/**
 * Object for the navigation destination for the contact screen.
 */
@Serializable
object ContactScreen

@Composable
fun ContactScreen(
    modifier: Modifier = Modifier,
){
    val mailIntent = Intent(Intent.ACTION_VIEW,("mailto:" + "feedback@tower-assist.de").toUri())
    val phoneIntent = Intent(Intent.ACTION_DIAL,("tel:" + "+49 173 8406203").toUri())
    val urlIntent = Intent(Intent.ACTION_VIEW, "https://tower-assist.de/".toUri())
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Kontakt",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Wir freuen uns über deine Fragen und Anregungen. Schreib uns jederzeit eine Mail an:",
            modifier = Modifier.padding(16.dp)
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
        Text(
            text = "Du kannst uns auch anrufen unter der Nummer: ",
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "+49 173 8406203",
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable(onClick = {
                    context.startActivity(phoneIntent)
                })
                .padding(16.dp)
        )
        Text(
            text = "Wir sind von Montag bis Freitag zwischen 9 und 17 Uhr erreichbar.\n" +
                   "Weitere Infos findest du auf unserer Webseite unter:",
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "https://tower-assist.de/",
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable(onClick = {
                    context.startActivity(urlIntent)
                })
                .padding(16.dp)
        )
        Text(
            text = "\n"+
                    "\n"+
                    "\n"+
                    "\n"+
                    "\n"+
                    "Tower Fernassistanz ist ein Angebot von:\n"+
                    "\n" +
                    "Bathildisheim e.V.\n" +
                    "Bathildisstraße 7\n" +
                    "34454 Bad Arolsen"
        )
    }
}