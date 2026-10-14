package com.walhalla.boilerplate.domain.executor

import java.util.concurrent.Future

interface Executor {
    fun execute(runnable: Runnable): Future<*>?
}
