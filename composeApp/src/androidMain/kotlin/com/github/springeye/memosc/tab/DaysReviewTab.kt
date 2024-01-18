package com.github.springeye.memosc.tab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
object DaysReviewTab: Tab {
    @Composable
    override fun Content() {
        Text("home2")
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "每次回顾"
            val icon = rememberVectorPainter(Icons.Outlined.CalendarToday)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

}