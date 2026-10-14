package com.walhalla.boilerplate.domain.executor

interface MainThread {
    fun post(runnable: Runnable)
}
