package com.github.springeye.memosc.ui.home.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.springeye.memosc.components.CardItem
import com.github.springeye.memosc.components.ITextField

object SettingsTab:Tab{
    @Composable
    override fun Content() {
        Scaffold {
            CardItem(modifier = Modifier.verticalScroll(rememberScrollState()).padding(10.dp).fillMaxWidth()) {
                Column {
                    ITextField("", onValueChange = {}, placeholder = {
                        Text("input server address")
                    })
                }
            }
        }
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