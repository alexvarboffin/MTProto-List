package com.walhalla.mtprotocompose

import android.app.Application
import com.walhalla.mtproto.shared.repository.ProxyRepository
import com.walhalla.mtprotocompose.data.LockedItemsStore

class MtprotoApplication : Application() {
    lateinit var repository: ProxyRepository
        private set
    lateinit var lockedItemsStore: LockedItemsStore
        private set

    override fun onCreate() {
        super.onCreate()
        repository = ProxyRepository.create()
        lockedItemsStore = LockedItemsStore(this)
    }
}
