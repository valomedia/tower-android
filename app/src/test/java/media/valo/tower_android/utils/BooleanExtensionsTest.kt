/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

//
//  BooleanExtensionsTest.kt
//  Tower_Android
//
//  Created by:
//      * Yatsar (Agent)
//

class BooleanExtensionsTest {

    @Test
    fun `then returns value when receiver is true`() {
        assertEquals("visible", true then "visible")
    }

    @Test
    fun `then returns null when receiver is false`() {
        assertNull(false then "hidden")
    }

}
