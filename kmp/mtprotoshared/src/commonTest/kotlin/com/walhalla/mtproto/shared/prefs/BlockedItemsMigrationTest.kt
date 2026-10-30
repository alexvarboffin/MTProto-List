package com.walhalla.mtproto.shared.prefs

import com.walhalla.mtproto.shared.config.AppConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BlockedItemsMigrationTest {
    private val defaults = setOf("4", "6", "7", "9")

    @Test
    fun copiesOnlyAStringSet() {
        val copied = BlockedItemsMigration.stringSetOrNull(setOf("4", "6"))
        assertEquals(setOf("4", "6"), copied)
        assertEquals(setOf("4", "6"), BlockedItemsMigration.valuesToStore(setOf("4", "6"), defaults))
    }

    @Test
    fun wrongTypesFallBackToDefault() {
        assertNull(BlockedItemsMigration.stringSetOrNull("4,6,7,9"))
        assertNull(BlockedItemsMigration.stringSetOrNull(4))
        assertNull(BlockedItemsMigration.stringSetOrNull(setOf(4, 6)))
        assertNull(BlockedItemsMigration.stringSetOrNull(setOf("4", 6)))
        assertEquals(defaults, BlockedItemsMigration.valuesToStore("4,6,7,9", defaults))
        assertEquals(defaults, BlockedItemsMigration.valuesToStore(setOf("4", 6), defaults))
        assertEquals(defaults, BlockedItemsMigration.valuesToStore(true, defaults))
        assertEquals(defaults, BlockedItemsMigration.valuesToStore("ru", defaults))
    }

    @Test
    fun nightModeAndLanguageAreNotTheBlockedSet() {
        val legacy = mapOf<String, Any?>(
            BlockedItemsMigration.KEY to setOf("1", "2"),
            "NightMode" to true,
            "pref_key_lang" to "ru",
        )
        assertEquals(
            setOf("1", "2"),
            BlockedItemsMigration.valuesToStore(legacy[BlockedItemsMigration.KEY], defaults),
        )
        assertEquals(defaults, BlockedItemsMigration.valuesToStore(legacy["NightMode"], defaults))
        assertEquals(defaults, BlockedItemsMigration.valuesToStore(legacy["pref_key_lang"], defaults))
    }

    @Test
    fun flagIsSetOnlyAfterTheValueWriteIsVisible() {
        val store = FakeStore()
        val ran = BlockedItemsMigration.run(
            alreadyMigrated = false,
            legacyValue = setOf("8"),
            defaultStrings = defaults,
            commitValues = store::commitValues,
            readBack = store::readBack,
            commitFlag = store::commitFlag,
        )
        assertTrue(ran)
        assertEquals(setOf("8"), store.values)
        assertTrue(store.migrated)
    }

    @Test
    fun failedCommitDoesNotSetTheFlag() {
        val store = FakeStore(commitSucceeds = false)
        val ran = BlockedItemsMigration.run(
            alreadyMigrated = false,
            legacyValue = setOf("8"),
            defaultStrings = defaults,
            commitValues = store::commitValues,
            readBack = store::readBack,
            commitFlag = store::commitFlag,
        )
        assertFalse(ran)
        assertFalse(store.migrated)
        assertNull(store.values)
    }

    @Test
    fun mismatchedReadBackDoesNotSetTheFlag() {
        val store = FakeStore(readBackOverride = setOf("1"))
        val ran = BlockedItemsMigration.run(
            alreadyMigrated = false,
            legacyValue = setOf("8"),
            defaultStrings = defaults,
            commitValues = store::commitValues,
            readBack = store::readBack,
            commitFlag = store::commitFlag,
        )
        assertFalse(ran)
        assertFalse(store.migrated)
    }

    @Test
    fun repeatMigrationDoesNotRun() {
        var commits = 0
        val ran = BlockedItemsMigration.run(
            alreadyMigrated = true,
            legacyValue = setOf("8"),
            defaultStrings = defaults,
            commitValues = {
                commits += 1
                true
            },
            readBack = { null },
            commitFlag = { true },
        )
        assertFalse(ran)
        assertEquals(0, commits)
    }

    @Test
    fun lockedDefaultsStayFourSixSevenNine() {
        val parsed = AppConfig.LOCKED_MTPROTO_POSITIONS
            .split(',')
            .map { it.trim().toInt() }
            .toSet()
        assertEquals(setOf(4, 6, 7, 9), parsed)
    }

    private class FakeStore(
        private val commitSucceeds: Boolean = true,
        private val readBackOverride: Set<String>? = null,
    ) {
        var values: Set<String>? = null
        var migrated: Boolean = false

        fun commitValues(values: Set<String>): Boolean {
            if (!commitSucceeds) return false
            this.values = values
            return true
        }

        fun readBack(): Set<String>? = readBackOverride ?: values

        fun commitFlag(): Boolean {
            migrated = true
            return true
        }
    }
}
