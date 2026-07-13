/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
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
