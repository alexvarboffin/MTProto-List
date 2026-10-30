package com.walhalla.mtproto.shared.list

import com.walhalla.mtprotolist.entity.MtprotoProxy

data class IndexedMtproto(
    val sourceIndex: Int,
    val proxy: MtprotoProxy,
)

fun List<MtprotoProxy?>.withLegacySourceIndex(): List<IndexedMtproto> {
    return indexPreservingHoles(this).map { (sourceIndex, proxy) ->
        IndexedMtproto(sourceIndex, proxy)
    }
}
