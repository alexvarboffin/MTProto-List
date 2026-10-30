package com.walhalla.mtprotocompose.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtproto.shared.prefs.BlockedItemsMigration

class LockedItemsStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val defaultLocked = AppConfig.LOCKED_MTPROTO_POSITIONS
        .split(',')
        .mapNotNull { it.trim().toIntOrNull() }
        .toSet()

    init {
        migrateFromLegacy(context)
    }

    fun lockedPositions(): Set<Int> {
        val stored = prefs.getStringSet(KEY_LOCKED, null)
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: defaultLocked
        return stored
    }

    fun isLocked(position: Int): Boolean = lockedPositions().contains(position)

    fun unlock(position: Int) {
        val updated = lockedPositions().toMutableSet()
        updated.remove(position)
        prefs.edit {
            putStringSet(KEY_LOCKED, updated.map(Int::toString).toSet())
        }
    }

    private fun migrateFromLegacy(context: Context) {
        if (prefs.getBoolean(BlockedItemsMigration.MIGRATED_FLAG, false)) return
        val legacy = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        val copied = BlockedItemsMigration.entriesToCopy(legacy.all, prefs.all.keys)
        for ((key, value) in copied) {
            putPreference(editor, key, value)
        }
        editor.putBoolean(BlockedItemsMigration.MIGRATED_FLAG, true).apply()
    }

    private fun putPreference(editor: SharedPreferences.Editor, key: String, value: Any) {
        when (value) {
            is Set<*> -> editor.putStringSet(key, value.mapNotNull { it as? String }.toSet())
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
        }
    }

    companion object {
        private const val PREFS_NAME = "mtproto_compose_prefs"
        private const val KEY_LOCKED = "blocked_items_"
    }
}
