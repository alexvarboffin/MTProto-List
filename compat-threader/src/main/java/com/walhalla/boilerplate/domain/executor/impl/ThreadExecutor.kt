package com.walhalla.boilerplate.domain.executor.impl

import com.walhalla.boilerplate.domain.executor.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future

/** Local stub: D:\dev\android\Compatibility\threader is ransomware-encrypted. */
class ThreadExecutor : Executor {
    private val pool = Executors.newCachedThreadPool()

    override fun execute(runnable: Runnable): Future<*>? = pool.submit(runnable)

    companion object {
        @JvmStatic
        fun getInstance(): ThreadExecutor = Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = ThreadExecutor()
    }
}
