package com.walhalla.mtproto.shared.list

/**
 * One child in `orderByChild("update_at")` order, before the legacy reverse.
 * [updateAt] is null when the child has no numeric `update_at` (missing or JSON null).
 * A decoded model must not substitute 0 for that absence.
 */
data class UpdateAtChild<T>(
    val key: String,
    val updateAt: Long?,
    val value: T?,
)

private val INTEGER_KEY = Regex("^-?(0|[1-9]\\d*)$")

/**
 * Firebase key order: 32-bit integer keys numerically, and those keys before
 * every other key. Remaining keys are lexicographical. "2" is before "10".
 */
fun compareFirebaseKeys(left: String, right: String): Int {
    if (left == right) return 0
    val leftInt = firebaseIntegerKey(left)
    val rightInt = firebaseIntegerKey(right)
    return when {
        leftInt != null && rightInt != null -> leftInt.compareTo(rightInt)
        leftInt != null -> -1
        rightInt != null -> 1
        else -> left.compareTo(right)
    }
}

fun compareUpdateAt(left: Long?, right: Long?): Int {
    return when {
        left == null && right == null -> 0
        left == null -> -1
        right == null -> 1
        else -> left.compareTo(right)
    }
}

fun <T> orderByChildUpdateAt(children: List<UpdateAtChild<T>>): List<UpdateAtChild<T>> {
    return children.sortedWith { left, right ->
        val byValue = compareUpdateAt(left.updateAt, right.updateAt)
        if (byValue != 0) byValue else compareFirebaseKeys(left.key, right.key)
    }
}

private fun firebaseIntegerKey(key: String): Int? {
    if (!INTEGER_KEY.matches(key)) return null
    val value = key.toLongOrNull() ?: return null
    if (value < Int.MIN_VALUE || value > Int.MAX_VALUE) return null
    return value.toInt()
}
