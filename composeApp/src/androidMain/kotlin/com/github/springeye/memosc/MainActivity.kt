package com.github.springeye.memosc


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
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
import com.github.springeye.memosc.tab.DaysReviewTab
import com.github.springeye.memosc.tab.ExploreTab
import com.github.springeye.memosc.tab.HomeTab
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KoinApplication(application = {
                modules(appModule, homeModule)
            }) {
                val koin= getKoin()
                val kamelConfig= KamelConfig {
                    takeFrom(KamelConfig.Default)
                    fetcher(Base64ImageFetcher())
                    httpFetcher {
                        install(HttpCookies){
                            this.storage=koin.get()
                        }
                    }
                }
                CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
                    TabNavigator(HomeTab) {
                        Scaffold(
                            content = {padding->
                                println(padding)
                                CurrentTab()
                            },
                            bottomBar = {
                                BottomNavigation {
                                    TabNavigationItem(HomeTab)
                                    TabNavigationItem(ExploreTab)
                                    TabNavigationItem(DaysReviewTab)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }
    )
}
@Preview
@Composable
fun AppAndroidPreview() {
}