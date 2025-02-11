/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.elements

//
//  Logo.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import media.valo.tower_android.R
import media.valo.tower_android.ui.theme.TowerTheme
import media.valo.tower_android.ui.theme.towerTextStyle

/**
 * The TOWER logo with the TOWER word mark underneath.
 *
 * @param modifier  `Modifier` for this element.
 */
@Composable
fun Logo(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
        )
        Text(
            text = "TOWER",
            style = towerTextStyle,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )
    }

}

/**
 * `Preview` for `Logo`.
 */
@Preview(showBackground = true, locale = "de-rDE")
@Composable
fun LogoPreview() {
    TowerTheme {
        Logo()
    }
}
