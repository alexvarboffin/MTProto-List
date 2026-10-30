package com.walhalla.mtproto.shared.prefs

object BlockedItemsMigration {
    const val KEY = "blocked_items_"
    const val MIGRATED_FLAG = "migrated_v1"

    /**
     * Legacy stores one string set under [KEY]. Anything else is not a set of
     * strings and must not be copied.
     * Returns null when the caller should use the default locked positions.
     */
    fun stringSetOrNull(value: Any?): Set<String>? {
        if (value !is Set<*>) return null
        val strings = LinkedHashSet<String>(value.size)
        for (item in value) {
            if (item !is String) return null
            strings.add(item)
        }
        return strings
    }

    fun valuesToStore(legacyValue: Any?, defaultStrings: Set<String>): Set<String> {
        return stringSetOrNull(legacyValue) ?: defaultStrings
    }

    /**
     * @return true only when the migrated flag was committed.
     * A failed value write or a read-back that does not match leaves the flag unset,
     * so a later launch can try again. [alreadyMigrated] skips the whole attempt.
     */
    fun run(
        alreadyMigrated: Boolean,
        legacyValue: Any?,
        defaultStrings: Set<String>,
        commitValues: (Set<String>) -> Boolean,
        readBack: () -> Set<String>?,
        commitFlag: () -> Boolean,
    ): Boolean {
        if (alreadyMigrated) return false
        val desired = valuesToStore(legacyValue, defaultStrings)
        if (!commitValues(desired)) return false
        val seen = try {
            readBack()
        } catch (e: ClassCastException) {
            null
        } ?: return false
        if (seen != desired) return false
        return commitFlag()
    }
}
