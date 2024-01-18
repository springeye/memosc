package com.github.springeye.memosc

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import com.github.springeye.memosc.ui.app.AppScreen
import org.koin.compose.getKoin


@Composable
fun DesktopApp() {
     val colors = lightColorScheme(
         primary = Color(11, 107, 203),
//         onPrimary = Color.White,
         background = Color(244, 244, 245),
//         onBackground = Color.White,
         surface=Color(244, 244, 245),
//         onSurface = Color.White,

    )
    val toastState = remember { mutableStateOf<ToastState>(ToastState.Hidden) }
    val notification= createPopupNotification(toastState)
    val koin=getKoin()
    CompositionLocalProvider(
    ){
    MaterialTheme(colorScheme = colors) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            CompositionLocalProvider(
                LocalNotification provides notification
            ){
                Navigator(AppScreen){ navigator->
                    ScaleTransition(navigator)
                }
            }
            Toast(toastState)
        }
    }
    }
}
 val LocalNotification = staticCompositionLocalOf<PopupNotification> {
    noLocalProvidedFor("LocalNotification")
}
private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}
