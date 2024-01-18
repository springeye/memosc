package com.github.springeye.memosc.ui.home.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.springeye.memosc.components.EditMemo
import com.github.springeye.memosc.components.EditMemoCallback
import com.github.springeye.memosc.components.EditMemoState
import com.github.springeye.memosc.components.Heatmap
import com.github.springeye.memosc.components.ITextField
import com.github.springeye.memosc.components.MemoList
import com.github.springeye.memosc.components.rememberEditMemoState
import com.github.springeye.memosc.model.MemosVisibility
import com.github.springeye.memosc.model.Resource
import com.github.springeye.memosc.model.weekDays
import com.github.springeye.memosc.ui.AppModel
import com.github.springeye.memosc.ui.home.MemoModel
import kotlinx.coroutines.launch

class HomeTab : Tab {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {

        val model = getScreenModel<MemoModel>()
        val lazyPagingItems = model.pager.collectAsLazyPagingItems()
        val refreshing = lazyPagingItems.loadState.refresh == LoadStateLoading
        val appSettings = getScreenModel<AppModel>().state.collectAsState()

        val scope = rememberCoroutineScope()
        val state = rememberEditMemoState(host = appSettings.value.host, callback = object:EditMemoCallback(){
            override suspend fun onSubmit(
                state:EditMemoState,
                editId: Long,
                content: String,
                resources: List<Resource>,
                visibility: MemosVisibility
            ) {
                model.submit(editId,content,resources,visibility)
                    state.setNewMemo(null)
                    lazyPagingItems.refresh()
            }

            override suspend fun onCancle(state: EditMemoState) {
                super.onCancle(state)
                state.setNewMemo(null)
            }
            override suspend fun onUpload(state: EditMemoState, path: String) {
                val resource=model.upload(path)
                state.addResource(resource)
            }

            override suspend fun onRemoveResourced(state: EditMemoState, resource: Resource) {
            }

        })
        LaunchedEffect(lazyPagingItems.itemSnapshotList.items){
            model.fetchExtraInfo()
        }
        Scaffold(modifier = Modifier, floatingActionButton = {
            SmallFloatingActionButton(onClick = {
                lazyPagingItems.refresh()

            }, shape = FloatingActionButtonDefaults.smallShape) {
                androidx.compose.material3.Icon(Icons.Default.Refresh, "")
            }
        }) {
            Row(modifier = Modifier) {
                Box(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(Modifier.padding(top = 20.dp)) {

                            EditMemo(state)
                        }
                        MemoList(
                            lazyPagingItems,
                            appSettings.value.host,
                            onEdit = {
                                     state.setNewMemo(it)
                            } ,
                            onPin = {
                                scope.launch {
                                    model.setPininned(it)
                                    lazyPagingItems.refresh()
                                }
                            },
                            onRemove = {
                                scope.launch {
                                    model.remove(it)
                                    lazyPagingItems.refresh()
                                }
                            })
                    }

                }
                Column(
                    modifier = Modifier.fillMaxHeight().width(200.dp).padding(10.dp)
                ) {
                    ITextField(
                        value = model.query.content?:"",
                        onValueChange = {
                            model.filterContent(it)
                        },
                        singleLine = true,
                        placeholder = {
                            Text(
                                "搜索备忘录",
                                style = TextStyle.Default.copy(color = Color(156, 163, 175))
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.height(175.dp)) {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (day in weekDays) {
                                    Text(
                                        day,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
//                                    Text(
//                                        weekDays[0],
//                                        style = MaterialTheme.typography.labelSmall,
//                                        color = MaterialTheme.colorScheme.outline
//                                    )
//                                    Text(
//                                        weekDays[3],
//                                        style = MaterialTheme.typography.labelSmall,
//                                        color = MaterialTheme.colorScheme.outline
//                                    )
//                                    Text(
//                                        weekDays[6],
//                                        style = MaterialTheme.typography.labelSmall,
//                                        color = MaterialTheme.colorScheme.outline
//                                    )
                            }
                            Heatmap(model.matrix)
                        }
//                            Text("")
                    }
                }
            }
            if(state.editId>0){
                Dialog(onDismissRequest = {
                    state.setNewMemo(null)
                }){
                    EditMemo(state)
                }
            }
        }

    }


    override val options: TabOptions
        @Composable
        get() {
            val title = "主页"
            val icon = rememberVectorPainter(Icons.Outlined.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }




}
