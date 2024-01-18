package com.github.springeye.memosc
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform