package com.walhalla.mtproto.shared.prefs

object BlockedItemsMigration {
    const val KEY_PREFIX = "blocked_items_"
    const val MIGRATED_FLAG = "migrated_v1"

    /**
     * Keys to copy from the legacy default SharedPreferences into the Compose store.
     * Destination keys that already exist are left untouched.
     */
    fun entriesToCopy(
        legacy: Map<String, *>,
        destinationKeys: Set<String>,
    ): Map<String, Any> {
        val result = LinkedHashMap<String, Any>()
        for ((key, value) in legacy) {
            if (!key.startsWith(KEY_PREFIX) || key in destinationKeys || value == null) {
                continue
            }
            result[key] = value
        }
        return result
    }
}
