package com.walhalla.boilerplate.threading

import android.os.Handler
import android.os.Looper
import com.walhalla.boilerplate.domain.executor.MainThread

class MainThreadImpl : MainThread {
    private val handler = Handler(Looper.getMainLooper())

    override fun post(runnable: Runnable) {
        handler.post(runnable)
    }

    companion object {
        @JvmStatic
        fun getInstance(): MainThreadImpl = Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = MainThreadImpl()
    }
}
