package com.walhalla.mtprotolist


interface ProtoAdapterCallback<T> {
    fun categorySelected(position: Int, title: T)

    fun shareProxy(adapterPosition: Int, data: T)

    fun qrProxyCode(adapterPosition: Int, data: T)

    fun handleProxyIntent(adapterPosition: Int, data: T)

    fun viewProxy(adapterPosition: Int, data: T)

    fun copyClipboard(host: String)

    fun delete(data: T)

    fun info(data: T)

    fun copyPort(data: T)
}