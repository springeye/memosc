package com.github.springeye.memosc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.github.springeye.memosc.core.Base64ImageFetcher
import com.github.springeye.memosc.di.appModule
import com.github.springeye.memosc.di.homeModule

import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.ktor.client.plugins.cookies.HttpCookies
import org.koin.compose.KoinApplication
import org.koin.compose.getKoin

@Composable
fun Bootstrap() {

    KoinApplication(application = {
        modules(appModule, homeModule)
    }) {
        val koin=getKoin()
        val kamelConfig=KamelConfig {
            takeFrom(KamelConfig.Default)
            fetcher(Base64ImageFetcher())
            httpFetcher {
                install(HttpCookies){
                    this.storage=koin.get()
                }
                httpCache(1024 * 1024 * 1024  /* 1024 MiB */)
            }
            // 100 by default
            imageBitmapCacheSize = 500
            // 100 by default
            imageVectorCacheSize = 300
            // 100 by default
            svgCacheSize = 200
        }
        CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
            DesktopApp()
        }
    }
}

