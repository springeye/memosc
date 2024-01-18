package com.github.springeye.memosc.tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object SettingsTab:Tab{
    @Composable
    override fun Content() {
        Text("home2")
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "设置"
            val icon = rememberVectorPainter(Icons.Outlined.Settings)

            return remember {
                TabOptions(
                    index = 6u,
                    title = title,
                    icon = icon
                )
            }
        }

}