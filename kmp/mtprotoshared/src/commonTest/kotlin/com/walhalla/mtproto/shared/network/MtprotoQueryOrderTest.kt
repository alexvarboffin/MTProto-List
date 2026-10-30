package com.walhalla.mtproto.shared.network

import com.walhalla.mtproto.shared.list.UpdateAtChild
import com.walhalla.mtproto.shared.list.compareFirebaseKeys
import com.walhalla.mtproto.shared.list.orderByChildUpdateAt
import com.walhalla.mtproto.shared.list.withLegacySourceIndex
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MtprotoQueryOrderTest {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun missingUpdateAtSortsBeforeZeroAndKeysBreakTies() {
        val body = """
            {
              "z": {"host":"z","port":"1","secret":"s","update_at":0},
              "m": {"host":"m","port":"1","secret":"s"},
              "a": {"host":"a","port":"1","secret":"s","update_at":0}
            }
        """.trimIndent()
        val ordered = decodeMtprotoInQueryOrder(body, json).map { it?.host }
        assertEquals(listOf("m", "a", "z"), ordered)
        val visible = decodeMtprotoInQueryOrder(body, json).withLegacySourceIndex()
        assertEquals(listOf("z", "a", "m"), visible.map { it.proxy.host })
    }

    @Test
    fun numericKeysSortByNumberBeforeStringKeys() {
        assertTrue(compareFirebaseKeys("2", "10") < 0)
        assertTrue(compareFirebaseKeys("10", "a") < 0)
        val children = listOf(
            UpdateAtChild("10", 5L, "ten"),
            UpdateAtChild("a", 5L, "ay"),
            UpdateAtChild("2", 5L, "two"),
        )
        assertEquals(
            listOf("two", "ten", "ay"),
            orderByChildUpdateAt(children).map { it.value },
        )
    }

    @Test
    fun unparsedChildStillOccupiesItsOrderedSlot() {
        val body = """
            {
              "10": {"host":"ten","port":"1","secret":"s","update_at":1},
              "2": "not-an-object",
              "a": {"host":"ay","port":"1","secret":"s","update_at":1}
            }
        """.trimIndent()
        val ordered = decodeMtprotoInQueryOrder(body, json)
        assertEquals(3, ordered.size)
        assertEquals(null, ordered[0])
        assertEquals("ten", ordered[1]?.host)
        assertEquals("ay", ordered[2]?.host)
    }
}
