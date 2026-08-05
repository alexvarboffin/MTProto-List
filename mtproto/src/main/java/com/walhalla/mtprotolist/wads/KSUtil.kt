package com.walhalla.mtprotolist.wads

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.walhalla.ui.DLog.handleException

class KSUtil private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)


    val blockedItems: MutableSet<Int> = HashSet()

    fun initialize(data0: MutableSet<Int>) {
        val stringSet: MutableSet<String?> = HashSet()
        for (i in data0) {
            if (i != null) {
                stringSet.add(i.toString())
            }
        }
        val tmp: MutableSet<String> = sharedPreferences.getStringSet(BLOCKED_ITEMS_KEY, stringSet)!!
        for (s in tmp) {
            try {
                blockedItems.add(s.toInt())
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun unlockItem(position: Int) {
        blockedItems.remove(position)
        saveUnlockedItems()
    }

    fun isItemLocked(position: Int): Boolean {
        return blockedItems.contains(position)
    }

    fun saveUnlockedItems() {
        val stringSet: MutableSet<String> = HashSet<String>()
        for (item in blockedItems) {
            if (item != null) {
                stringSet.add(item.toString())
            }
        }
        sharedPreferences.edit().putStringSet(BLOCKED_ITEMS_KEY, stringSet).apply()
    }

    companion object {
        private const val BLOCKED_ITEMS_KEY = "blocked_items_"


        private var instance: KSUtil? = null

        // Метод для получения единственного экземпляра класса с синхронизацией
        @Synchronized
        fun getInstance(context: Context): KSUtil {
            if (instance == null) {
                instance = KSUtil(context)
            }
            return instance!!
        }
    }
}
