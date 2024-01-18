package com.github.springeye.memosc.ui.home.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.springeye.memosc.ui.home.NotifiModel

object InboxTab:Tab{
    @Composable
    override fun Content() {
        val model=getScreenModel<NotifiModel>()

        Column {
            Text("Inbox ${model.counter}")
            Button(onClick = {
                model.addItem("aa")
            }){
                Text("aa")
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "通知"
            val icon = rememberVectorPainter(Icons.Outlined.Inbox)

            return remember {
                TabOptions(
                    index = 4u,
                    title = title,
                    icon = icon
                )
            }
        }

}