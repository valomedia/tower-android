/******************************************************************************
 * Copyright (c) 2026 valo.media GmbH                                         *
 * All rights reserved.                                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Affero General Public License as             *
 * published by the Free Software Foundation, either version 3 of the         *
 * License, or (at your option) any later version.                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Affero General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Affero General Public License   *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package media.valo.tower_android.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

//
//  BooleanExtensionsTest.kt
//  Tower_Android
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
