package com.github.springeye.memosc
class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val versionCode: String
        get() = BuildKonfig.versionCode
}

actual fun getPlatform(): Platform = JVMPlatform()