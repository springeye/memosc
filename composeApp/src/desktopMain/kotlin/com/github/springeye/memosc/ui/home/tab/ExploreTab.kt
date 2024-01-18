package com.github.springeye.memosc.ui.home.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.springeye.memosc.ui.home.MemoModel

object ExploreTab:Tab{
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<MemoModel>()
        val lazyPagingItems = model.pager.collectAsLazyPagingItems()
        val refreshing = lazyPagingItems.loadState.refresh == LoadStateLoading
        val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh = {
            lazyPagingItems.refresh()
        })
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()
            .background(Color.Red)
            .pullRefresh(pullRefreshState)) {
            Text("探索")
            PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }

    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "探索"
            val icon = rememberVectorPainter(Icons.Outlined.Explore)

            return remember {
                TabOptions(
                    index = 3u,
                    title = title,
                    icon = icon
                )
            }
        }

}