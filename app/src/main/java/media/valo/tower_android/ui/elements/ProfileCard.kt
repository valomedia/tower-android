/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.elements

//
//  ProfileCard.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import media.valo.tower_android.ui.theme.TowerTheme

/**
 * An element that displays the username and legal name of the user.
 *
 * The main point of this is to make a good-looking display of whatever information is available,
 * even though some (or all) of it might be missing.
 *
 * @param username  The username of the user, if known.
 * @param firstName The first name of the user, if known.
 * @param lastName  The last name of the user, if known.
 * @param modifier  `Modifier` for this element.
 */
@Composable
fun ProfileCard(
    username: String?,
    firstName: String?,
    lastName: String?,
    modifier: Modifier = Modifier
) {
    if (!firstName.isNullOrBlank() || !lastName.isNullOrBlank()) {
        Card(
            firstLine = listOf(firstName, lastName)
                .filter { !it.isNullOrBlank() }
                .joinToString(" "),
            secondLine = username,
            modifier = modifier
        )
    } else {
        Card(
            firstLine = if (!username.isNullOrBlank()) username else "Unbekannter Benutzer",
            modifier = modifier
        )
    }
}

@Composable
private fun Card(
    firstLine: String,
    secondLine: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                firstLine,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )
            if (!secondLine.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    secondLine,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * `Preview` for `ProfileCard`.
 */
@Preview(showBackground = true, locale = "de-rDE")
@Composable
fun ProfileCardPreview() {
    TowerTheme {
        ProfileCard(
            username = "theo.test",
            firstName = "Theo",
            lastName = "Test",
            modifier = Modifier.padding(16.dp)
        )
    }
}
