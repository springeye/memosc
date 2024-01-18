package com.github.springeye.memosc.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.github.springeye.memosc.LocalNotification
import com.github.springeye.memosc.components.Heatmap
import com.github.springeye.memosc.components.ITextField
import com.github.springeye.memosc.core.Base64Image
import com.github.springeye.memosc.model.weekDays
import com.github.springeye.memosc.ui.home.tab.ArchivedTab
import com.github.springeye.memosc.ui.home.tab.DaysReviewTab
import com.github.springeye.memosc.ui.home.tab.ExploreTab
import com.github.springeye.memosc.ui.home.tab.HomeTab
import com.github.springeye.memosc.ui.home.tab.InboxTab
import com.github.springeye.memosc.ui.home.tab.ProfileTab
import com.github.springeye.memosc.ui.home.tab.ResourcesTab
import com.github.springeye.memosc.ui.home.tab.SettingsTab

import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

object HomeScreen : Screen {
    private fun readResolve(): Any = HomeScreen

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<MemoModel>()
        val user = screenModel.user
        val notification = LocalNotification.current
        val homeTab = remember {
            HomeTab()
        }
        val weekDays = remember { weekDays }
        TabNavigator(homeTab) {
            Scaffold() {
                Row() {
//                    val menus = listOf(
//                        MenuItem(Icons.Filled.Home, "主页"),
//                        MenuItem(Icons.Filled.Call, "每日回顾"),
//                        MenuItem(Icons.Filled.Call, "资源库"),
//                        MenuItem(Icons.Filled.Call, "探索"),
//                        MenuItem(Icons.Filled.Call, "通知"),
//                        MenuItem(Icons.Filled.Call, "已归档"),
//                        MenuItem(Icons.Filled.Call, "设置"),
//                    )
                    NavMenu(listOf(
                        homeTab,
//                        DaysReviewTab,
                        ResourcesTab,
//                        ExploreTab,
//                        InboxTab,
                        ProfileTab,
                        ArchivedTab,
                        SettingsTab
                    ), width = 200.dp, header = {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp)
                                .height(40.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            KamelImage(
                                asyncPainterResource(Base64Image(user?.avatarUrl ?: "")),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,            // crop the image if it's not a square
                                modifier = Modifier
                                    .aspectRatio(1F)                        // set the image aspect ratio to 1:1
                                    .clip(CircleShape)                       // clip to the circle shape
                            )
                            Text(
                                user?.displayName ?: "",
                                modifier = Modifier.padding(start = 5.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    })
                    Box(modifier = Modifier.weight(1F)) {
                        CurrentTab()
                    }

                }
            }
        }
    }
}

@Composable
fun RowScope.NavMenu(
    menus: List<Tab>,
    width: Dp = 180.dp,
    header: (@Composable LazyItemScope.() -> Unit) = {}
) {
    val tabNavigator = LocalTabNavigator.current
    LazyColumn(modifier = Modifier.width(width), horizontalAlignment = Alignment.Start) {
        item(content = header)
        items(menus) { item ->
            NavButton(item, tabNavigator.current == item) {
                tabNavigator.current = item;
            }
        }
    }
}

@Composable
fun NavButton(tab: Tab, selected: Boolean = false, onClick: () -> Unit) {
    val colors = CardDefaults.cardColors(
        containerColor = if (selected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent, //Card background color
    )
    Card(elevation = CardDefaults.cardElevation(0.dp),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(12.dp)),
        colors = colors,
        modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
            .clip(CardDefaults.shape)
            .clickable { onClick.invoke() }
            .pointerHoverIcon(PointerIcon.Hand)

    ) {
        Row(modifier = Modifier.padding(10.dp).fillMaxWidth()) {
            val icon = tab.options.icon
            if (icon != null) Image(icon, "", modifier = Modifier.padding(end = 10.dp).size(24.dp))
            Text(tab.options.title)
        }
    }

}