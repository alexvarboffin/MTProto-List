package com.walhalla.mtproto.shared.prefs

import com.walhalla.mtproto.shared.config.AppConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BlockedItemsMigrationTest {
    @Test
    fun copiesOnlyBlockedItemKeysThatAreMissing() {
        val legacy = mapOf<String, Any?>(
            "blocked_items_" to setOf("4", "6"),
            "blocked_items_extra" to "1",
            "other" to "nope",
            "blocked_items_skip" to null,
        )
        val copied = BlockedItemsMigration.entriesToCopy(
            legacy,
            destinationKeys = setOf("blocked_items_extra"),
        )
        assertEquals(setOf("blocked_items_"), copied.keys)
        assertEquals(setOf("4", "6"), copied["blocked_items_"])
    }

    @Test
    fun emptyLegacyCopiesNothing() {
        assertTrue(
            BlockedItemsMigration.entriesToCopy(emptyMap<String, Any?>(), emptySet<String>()).isEmpty(),
        )
    }

    @Test
    fun lockedPositionsMatchLegacyDefaults() {
        val parsed = AppConfig.LOCKED_MTPROTO_POSITIONS
            .split(',')
            .map { it.trim().toInt() }
            .toSet()
        assertEquals(setOf(4, 6, 7, 9), parsed)
    }
}
