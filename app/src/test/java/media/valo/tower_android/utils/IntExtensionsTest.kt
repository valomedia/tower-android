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

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

//
//  IntExtensionsTest.kt
//  Tower_Android
//

class IntExtensionsTest {

    @Test
    fun `isEven identifies zero and values divisible by two`() {
        assertTrue(0.isEven)
        assertTrue(2.isEven)
        assertTrue((-4).isEven)
    }

    @Test
    fun `isOdd identifies positive and negative values not divisible by two`() {
        assertTrue(1.isOdd)
        assertTrue((-3).isOdd)
    }

    @Test
    fun `isEven and isOdd are complementary`() {
        for (value in -10..10) {
            assertFalse(value.isEven && value.isOdd)
            assertTrue(value.isEven || value.isOdd)
        }
    }

}
