/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.news

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

//
//  NewsTest.kt
//  Tower_Android
//

class NewsTest {

    @Test
    fun `news entries are not empty`() {
        assertTrue(newsEntries.isNotEmpty())
    }

    @Test
    fun `every news entry has a version and at least one change`() {
        newsEntries.forEach { entry ->
            assertTrue(entry.version.isNotBlank())
            assertTrue(entry.changes.isNotEmpty())
            entry.changes.forEach { change -> assertTrue(change.isNotBlank()) }
        }
    }

    @Test
    fun `news entries have unique versions`() {
        val versions = newsEntries.map { it.version }
        assertEquals(versions.size, versions.distinct().size)
    }

    @Test
    fun `latest news version is the version of the first entry`() {
        assertEquals(newsEntries.first().version, latestNewsVersion)
    }

    @Test
    fun `news entries are ordered from newest to oldest`() {
        val versions = newsEntries.map { entry ->
            entry.version.split(".").map { part -> part.toInt() }
        }
        versions.zipWithNext { newer, older ->
            assertTrue(compareVersions(newer, older) > 0)
        }
    }

    /**
     * Compare two versions given as their numeric parts.
     *
     * @param first     The parts of the first version.
     * @param second    The parts of the second version.
     *
     * @return A positive number if the first version is newer, a negative number if it is older,
     *         and zero if both versions are the same.
     */
    private fun compareVersions(first: List<Int>, second: List<Int>): Int {
        for (index in 0 until maxOf(first.size, second.size)) {
            val comparison = (first.getOrElse(index) { 0 }).compareTo(second.getOrElse(index) { 0 })
            if (comparison != 0) {
                return comparison
            }
        }
        return 0
    }

}
