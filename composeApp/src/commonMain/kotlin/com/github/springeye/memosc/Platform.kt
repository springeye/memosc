package com.github.springeye.memosc
interface Platform {
    val name: String
    val versionCode:String
}

expect fun getPlatform(): Platform