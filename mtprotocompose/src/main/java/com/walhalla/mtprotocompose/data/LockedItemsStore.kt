package com.walhalla.mtprotocompose.data

import android.content.Context
import androidx.preference.PreferenceManager
import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtproto.shared.prefs.BlockedItemsMigration

class LockedItemsStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val defaultLocked = AppConfig.LOCKED_MTPROTO_POSITIONS
        .split(',')
        .mapNotNull { it.trim().toIntOrNull() }
        .toSet()
    private val defaultStrings = defaultLocked.map(Int::toString).toSet()

    init {
        migrateFromLegacy(context)
    }

    fun lockedPositions(): Set<Int> {
        val stored = try {
            prefs.getStringSet(BlockedItemsMigration.KEY, null)
        } catch (e: ClassCastException) {
            return defaultLocked
        }
        if (stored == null) return defaultLocked
        return try {
            stored.mapNotNull { it.toIntOrNull() }.toSet()
        } catch (e: ClassCastException) {
            defaultLocked
        }
    }

    fun isLocked(position: Int): Boolean = lockedPositions().contains(position)

    fun unlock(position: Int) {
        val updated = lockedPositions().toMutableSet()
        updated.remove(position)
        prefs.edit()
            .putStringSet(BlockedItemsMigration.KEY, updated.map(Int::toString).toHashSet())
            .apply()
    }

    private fun migrateFromLegacy(context: Context) {
        val alreadyMigrated = try {
            prefs.getBoolean(BlockedItemsMigration.MIGRATED_FLAG, false)
        } catch (e: ClassCastException) {
            false
        }
        val legacyValue = PreferenceManager.getDefaultSharedPreferences(context)
            .all[BlockedItemsMigration.KEY]
        BlockedItemsMigration.run(
            alreadyMigrated = alreadyMigrated,
            legacyValue = legacyValue,
            defaultStrings = defaultStrings,
            commitValues = { values ->
                prefs.edit()
                    .putStringSet(BlockedItemsMigration.KEY, HashSet(values))
                    .commit()
            },
            readBack = {
                prefs.getStringSet(BlockedItemsMigration.KEY, null)?.toSet()
            },
            commitFlag = {
                prefs.edit()
                    .putBoolean(BlockedItemsMigration.MIGRATED_FLAG, true)
                    .commit()
            },
        )
    }

    companion object {
        private const val PREFS_NAME = "mtproto_compose_prefs"
    }
}
