package com.github.springeye.memosc.ui.home.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.springeye.memosc.components.MemoList
import com.github.springeye.memosc.core.Base64Image
import com.github.springeye.memosc.ui.AppModel
import com.github.springeye.memosc.ui.home.ArchivedModel
import com.github.springeye.memosc.ui.home.MemoModel
import com.github.springeye.memosc.ui.home.ProfileModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

object ArchivedTab:Tab{
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<MemoModel>()
        val memoModel  =getScreenModel<ArchivedModel>()
        val settings   by getScreenModel<AppModel>().state.collectAsState()
        val user =screenModel.user
        val items=memoModel.getPagerByUserId(user?.id?:0L).collectAsLazyPagingItems()
        Scaffold {
            if(items.itemCount<=0){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text("没有任何数据")
                }
            }else{
                MemoList(items, settings.host,{},{},{})
            }

        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "已归档"
            val icon = rememberVectorPainter(Icons.Outlined.Archive)

            return remember {
                TabOptions(
                    index = 5u,
                    title = title,
                    icon = icon
                )
            }
        }

}