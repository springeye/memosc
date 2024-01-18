package com.github.springeye.memosc
import io.github.aakira.napier.Napier

expect fun initLogger()
object Logger{
    fun d(message: String, throwable: Throwable? = null, tag: String? = null){
        Napier.d(message,throwable,tag)
    }
    fun i(message: String, throwable: Throwable? = null, tag: String? = null){
        Napier.i(message,throwable,tag)
    }
    fun e(message: String, throwable: Throwable? = null, tag: String? = null){
        Napier.e(message,throwable,tag)
    }
    fun w(message: String, throwable: Throwable? = null, tag: String? = null){
        Napier.w(message,throwable,tag)
    }
    fun v(message: String, throwable: Throwable? = null, tag: String? = null){
        Napier.v(message,throwable,tag)
    }
}