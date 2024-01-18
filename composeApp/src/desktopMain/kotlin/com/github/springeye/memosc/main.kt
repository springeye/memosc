package com.github.springeye.memosc

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import javax.swing.JFrame

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
fun main() = application {
    initLogger()
    val state= rememberWindowState(position = WindowPosition(Alignment.Center), width = 900.dp, height =600.dp )

    Window(state=state,
        resizable = false,
        icon = painterResource("logo.png"),
        onCloseRequest = ::exitApplication, title = "Memosc", undecorated = true) {
        Column(modifier = Modifier) {

            Row(
                modifier = Modifier/*.background(color = Color(75, 75, 75))*/
                    .fillMaxWidth()
                    .height(35.dp)
                    .background(Color(0xffdddddd))
                    .onClick(onDoubleClick = {
                        if (window.extendedState == JFrame.MAXIMIZED_BOTH) {
                            window.extendedState = JFrame.NORMAL
                        } else {
                            window.extendedState = JFrame.MAXIMIZED_BOTH
                        }

                    }){}
                    .padding(start = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WindowDraggableArea(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = window.title, color = Color.Black)
                }
                Row {
                    WinButton(
                        onClick = {
                            window.extendedState = JFrame.ICONIFIED
                        },
                        icon = painterResource("images/ic_chrome_minimize.png")
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    WinButton(
                        onClick = {
                            if (window.extendedState == JFrame.MAXIMIZED_BOTH) {
                                window.extendedState = JFrame.NORMAL
                            } else {
                                window.extendedState = JFrame.MAXIMIZED_BOTH
                            }

                        },
                        icon = if(state.placement!=WindowPlacement.Maximized)painterResource("images/ic_chrome_maximize.png") else  painterResource("images/ic_chrome_unmaximize.png")
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    WinButton(
                        onClick = {
                            exitApplication()
                        },
                        icon = painterResource("images/ic_chrome_close.png"),
                        backgroundColor = ColorScheme(Color.Transparent,Color.Red.copy(alpha =0.7F),Color.Red.copy(alpha =1.0F)),
                        iconColor = ColorScheme(Color.Black.copy(alpha = 0.8956F),Color.White.copy(alpha =0.7F),Color.White.copy(alpha =1.0F))
                    )
                }
            }
                    Box(Modifier.background(Color(0xffdddddd)).padding(start = 1.dp, end = 1.dp, bottom = 1.dp)){
                        Bootstrap()
                    }
        }

    }

}
 data class ColorScheme(
    val normal:Color,
    val hovered:Color,
    val pressed:Color,
)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WinButton(
    text: String = "",
    icon: Painter,
    backgroundColor: ColorScheme = ColorScheme(Color.Transparent,Color.Black.copy(alpha =0.0373F),Color.Black.copy(alpha =0.0241F)),
    iconColor: ColorScheme = ColorScheme(Color.Black.copy(alpha = 0.8956F),Color.Black.copy(alpha =0.8956F),Color.Black.copy(alpha =0.6063F)),
    onClick: () -> Unit = {},
) {
    var bgColor by remember { mutableStateOf(backgroundColor.normal) }
    var icColor by remember { mutableStateOf(iconColor.normal) }
    var selected by remember { mutableStateOf(false) }
    Box(modifier = Modifier
        .background(bgColor)
        .clickable {
            selected=!selected;
            bgColor = if(selected){
                backgroundColor.pressed
            }else{
                backgroundColor.normal
            }
            icColor = if(selected){
                iconColor.pressed
            }else{
                iconColor.normal
            }
            onClick.invoke()
        }
        .onPointerEvent(PointerEventType.Enter) {
            bgColor = backgroundColor.hovered
            icColor = iconColor.hovered
        }
        .onPointerEvent(PointerEventType.Exit) {
            bgColor = backgroundColor.normal
            icColor = iconColor.normal
        }        .padding(12.dp)) {
        Image(icon,"", colorFilter = ColorFilter.tint(icColor))
    }
    }

@Preview
@Composable
fun AppDesktopPreview() {
    DesktopApp()
}