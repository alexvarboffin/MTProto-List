package com.walhalla.mtproto.shared.list

/**
 * Legacy `f1` walks `/a` in `orderByChild("update_at")` order and then reverses
 * the list. A child that does not become a proxy stays in that list as a hole,
 * so later rows keep their source index.
 *
 * [inQueryOrder] is that pre-reverse order. Null is a hole.
 * The returned pairs are `(sourceIndex, value)` for rows the UI can draw.
 * Holes are omitted here; [sourceIndex] is still the reversed list index.
 */
fun <T> indexPreservingHoles(inQueryOrder: List<T?>): List<Pair<Int, T>> {
    return inQueryOrder.asReversed().mapIndexedNotNull { index, value ->
        if (value == null) null else index to value
    }
}
