package com.walhalla.mtprotocompose.data

import android.content.Context
import androidx.core.content.edit

class LockedItemsStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val defaultLocked = setOf(4, 6, 7, 9)

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

    companion object {
        private const val PREFS_NAME = "mtproto_compose_prefs"
        private const val KEY_LOCKED = "blocked_items_"
    }
}
