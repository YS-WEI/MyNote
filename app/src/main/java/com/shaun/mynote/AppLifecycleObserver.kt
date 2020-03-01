package com.shaun.mynote

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent


class AppLifecycleObserver : LifecycleObserver {

    interface CallbackListener {
        fun onEnterForeground()
        fun onEnterBackground()
    }
    private val callbackListener : CallbackListener

    constructor(listener : CallbackListener) {
        callbackListener = listener
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() { //run the code we need
        callbackListener.onEnterForeground()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() { //run the code we need
        callbackListener.onEnterBackground()
    }
}