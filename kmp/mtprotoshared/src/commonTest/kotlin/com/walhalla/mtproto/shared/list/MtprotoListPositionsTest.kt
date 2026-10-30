package com.walhalla.mtproto.shared.list

import com.walhalla.mtprotolist.entity.MtprotoProxy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class MtprotoListPositionsTest {
    @Test
    fun holeBeforeAndBetweenLockedPositionsDoesNotShiftThem() {
        // Query order is orderByChild("update_at") ascending: q0 is oldest, q9 is newest.
        // After the legacy reverse, source index i is q(9 - i).
        // Locked source indexes 4, 6, 7, 9 are therefore q5, q3, q2, q0.
        val query = (0..9).map { "q$it" as String? }.toMutableList()
        query[7] = null // display index 2, before the locked row at 4
        query[4] = null // display index 5, between locked rows 4 and 6

        val visible = indexPreservingHoles(query)
        val byIndex = visible.toMap()

        assertEquals("q5", byIndex[4])
        assertEquals("q3", byIndex[6])
        assertEquals("q2", byIndex[7])
        assertEquals("q0", byIndex[9])
        assertFalse(byIndex.containsKey(2))
        assertFalse(byIndex.containsKey(5))
        assertEquals(listOf(0, 1, 3, 4, 6, 7, 8, 9), visible.map { it.first })
    }

    @Test
    fun droppingHolesBeforeReverseWouldPointLocksAtOtherRows() {
        val query = (0..9).map { "q$it" as String? }.toMutableList()
        query[7] = null
        query[4] = null
        val shifted = query.filterNotNull().asReversed()
        assertEquals("q3", shifted[4])
        assertEquals("q5", indexPreservingHoles(query).toMap()[4])
    }

    @Test
    fun indexedProxyUsesSourceIndexNotVisualRow() {
        fun proxy(host: String) = MtprotoProxy(host, "1", "s", "", true)
        val visible = listOf(proxy("q0"), null, proxy("q2")).withLegacySourceIndex()
        assertEquals(0, visible[0].sourceIndex)
        assertEquals("q2", visible[0].proxy.host)
        assertEquals(2, visible[1].sourceIndex)
        assertEquals("q0", visible[1].proxy.host)
    }
}
