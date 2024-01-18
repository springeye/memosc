package com.github.springeye.memosc

import android.app.Application

class MemoscApplication: Application() {
    override fun onCreate() {
        super.onCreate()
//        startKoin {
//            // Log Koin into Android logger
//            androidLogger()
//            // Reference Android context
//            androidContext(this@MemoscApplication)
//            // Load modules
//            modules(appModule, homeModule)
//        }
    }
}