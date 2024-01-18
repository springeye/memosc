package com.github.springeye.memosc.ui.home.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.window.Dialog
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.springeye.memosc.components.EditMemo
import com.github.springeye.memosc.components.EditMemoCallback
import com.github.springeye.memosc.components.EditMemoState
import com.github.springeye.memosc.components.MemoList
import com.github.springeye.memosc.components.rememberEditMemoState
import com.github.springeye.memosc.core.Base64Image
import com.github.springeye.memosc.model.Memo
import com.github.springeye.memosc.model.MemosVisibility
import com.github.springeye.memosc.model.Resource
import com.github.springeye.memosc.ui.AppModel
import com.github.springeye.memosc.ui.home.MemoModel
import com.github.springeye.memosc.ui.home.ProfileModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

object ProfileTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "个人资料"
            val icon = rememberVectorPainter(Icons.Outlined.Person);
            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val profileModel  =getScreenModel<ProfileModel>()
        val memoModel  =getScreenModel<MemoModel>()

        val settings   by getScreenModel<AppModel>().state.collectAsState()
        val user =memoModel.user
        val items=profileModel.getPagerByUserId(user?.id?:0L).collectAsLazyPagingItems()
        val editState= rememberEditMemoState(Memo.empty(), settings.host, callback = object:
            EditMemoCallback() {
            override suspend fun onCancle(state: EditMemoState) {
                super.onCancle(state)
                state.setNewMemo(null)
            }
            override suspend fun onUpload(state: EditMemoState, path: String) {
                val resource=memoModel.upload(path)
                state.addResource(resource)
            }
            override suspend fun onSubmit(
                state: EditMemoState,
                editId: Long,
                content: String,
                resources: List<Resource>,
                visibility: MemosVisibility
            ) {
                memoModel.submit(editId,content,resources,visibility)
            }
            })
        Scaffold {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp).fillMaxSize()
                , horizontalAlignment = Alignment.CenterHorizontally
            ) {
                KamelImage(
                    asyncPainterResource(Base64Image(user?.avatarUrl ?: "")),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,            // crop the image if it's not a square
                    modifier = Modifier
                        .height(80.dp)
                        .aspectRatio(1F)                        // set the image aspect ratio to 1:1
                        .clip(CircleShape)                       // clip to the circle shape
                )
                Text(
                    user?.displayName ?: "",
                    modifier = Modifier.padding(start = 5.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                MemoList(items, settings.host,{
                    editState.setNewMemo(it)
                },{},{})
                if(editState.editId>0){
                    Dialog(onDismissRequest = {
                        editState.setNewMemo(null)
                    }){
                        EditMemo(editState)
                    }
                }
            }

        }
    }
}