package com.github.springeye.memosc

import Platform
import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val versionCode: Long
        get() = TODO("Not yet implemented")
}

actual fun getPlatform(): Platform = AndroidPlatform()