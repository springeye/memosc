package com.github.springeye.memosc.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.github.springeye.memosc.LocalNotification
import com.github.springeye.memosc.model.Memo
import com.github.springeye.memosc.model.MemosVisibility
import com.github.springeye.memosc.model.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.swing.Icon

abstract class EditMemoCallback {
    open suspend fun onSubmit(state:EditMemoState,editId:Long,content: String, resources: List<Resource>, visibility: MemosVisibility){}
    open suspend fun onUpload(state:EditMemoState,path: String){}
    open suspend fun onContentChange(state:EditMemoState,text:String){}
    open suspend fun onRemoveResourced(state:EditMemoState, resource:Resource){}
    open suspend fun onCancle(state:EditMemoState){}
    companion object{
        val DEFAULT = object:EditMemoCallback(){}
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Preview
@Composable
fun EditMemo(
    state: EditMemoState = rememberEditMemoState(host = ""),
    ) {
    LaunchedEffect(state){
        snapshotFlow{
            state.content
        }.collect{
            state.callback.onContentChange(state,it)
        }

    }
    val scope = state.scope
    val notification = LocalNotification.current
    CardItem(
        radius = 10.dp,
        borderColor = Color(0xccCCCCCC),
        borderWidth = 1.dp,
        paddingValues = PaddingValues(start = 10.dp, end = 10.dp, bottom = 10.dp),
        hoverBorderColor = Color(0xccCCCCCC),
    ) {
        Column(Modifier.fillMaxWidth().wrapContentHeight()) {
            ITextField(
                state.content,
                onValueChange = state::updateContent,
                minLines = 2,
                maxLines = 10,
                placeholder = {
                    Text(
                        "任何想法",
                        style = TextStyle.Default.copy(color = Color(156, 163, 175))
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(), // Here I have decreased the height
                shape = RoundedCornerShape(0.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,

                    ),

                )
            var showFilePicker by remember { mutableStateOf(false) }
            Row {
                Icon(Icons.Default.Tag, "")
                Icon(Icons.Default.Photo, "", Modifier.padding(start = 10.dp).clickable {
                    showFilePicker = true
                })
                Icon(Icons.Default.Attachment, "", Modifier.padding(start = 10.dp))
            }
            Row {


                val fileType = listOf("jpg", "png")
                FilePicker(show = showFilePicker, fileExtensions = fileType) { file ->
                    showFilePicker = false
//                        println(file?.path)
                    file?.path?.let {
                        scope.launch {
                            state.callback.onUpload(state,it)
                        }
                    }
                    // do something with the file
                }
            }
            Divider(Modifier.padding(top = 10.dp, bottom = 10.dp))

            FlowRow() {
                state.resources.forEach { item ->
                    AnimatedContent(targetState = item) {
                        ResourceItem(item, state.host) {
                            state.removeResource(it)
                            scope.launch {
                                state.callback.onRemoveResourced(state, it)
                            }
                        }
                    }
                }
            }
            Row(Modifier.padding(vertical = 10.dp)) {
                VisibilityButton()
                Spacer(Modifier.weight(1f))
                if(state.editId>0){
                    SubmitButton("取消",Icons.Default.Cancel,true,Modifier.padding(end = 10.dp)){
                        scope.launch {
                            state.callback.onCancle(state)
                        }
                    }
                }
                SubmitButton("保存",Icons.Default.Send,state.content.isNotEmpty()){
                    scope.launch {
                        state.callback.onSubmit(
                            state,
                            state.editId,
                            state.content,
                            state.resources,
                            state.visibility
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun SubmitButton(text: String, icon: ImageVector, enable: Boolean,modifier: Modifier=Modifier, onSubmit: () -> Unit) {
    Button(
        enabled = enable,
        onClick = onSubmit,
        contentPadding = PaddingValues(horizontal = 10.dp),
        shape = RoundedCornerShape(5.dp),
        modifier = modifier
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
            .height(30.dp)
            .pointerHoverIcon(PointerIcon.Hand)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text)
            Icon(
                icon, "",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 5.dp)
            )
        }
    }
}

@Composable
fun VisibilityButton() {
    val notification = LocalNotification.current
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.clip(RoundedCornerShape(5.dp))
        .background(Color(0x99eeeeee))
        .clickable {
            expanded = !expanded
        }.padding(vertical = 5.dp, horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier.wrapContentWidth()
        ) {
            val tint = Color(156, 163, 175)
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "More",
                modifier = Modifier.size(20.dp),
                tint = tint
            )
            Text(
                "私有",
                style = TextStyle.Default.copy(color = tint),
                modifier = Modifier.padding(horizontal = 5.dp)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "More",
                modifier = Modifier.size(20.dp),
                tint = tint


            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Load") },
                onClick = { notification.showPopUpMessage("load") }
            )
            DropdownMenuItem(
                text = { Text("Save") },
                onClick = { notification.showPopUpMessage("save") }
            )
        }
    }
}

@Composable
fun ResourceItem(item: Resource, host: String,removeResource:  (Resource)->Unit) {

    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .padding(end = 5.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        KamelImage(
            asyncPainterResource(item.uri(host)),
            "",
            Modifier.size(20.dp)

        )
        Text(
            item.filename,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 5.dp)
        )
        Icon(Icons.Default.Close, "", modifier = Modifier.clickable {
            removeResource(item)
        })
    }
}

class EditMemoState(
    private val initContent:String="",
    private val initResources:List<Resource> = listOf(),
    private val initVisibility: MemosVisibility = MemosVisibility.PUBLIC,
    internal val host:String,
    internal val callback:EditMemoCallback=EditMemoCallback.DEFAULT,
    internal val scope: CoroutineScope
    ){
    var editId by mutableStateOf(0L)
    var content by mutableStateOf(initContent)
        private set
    var resources = mutableStateListOf<Resource>(*initResources.toTypedArray())
        private set
    var visibility by mutableStateOf(initVisibility)
        private set
    fun updateContent(content: String) {
        this.content = content
    }
    fun addResource(resource: Resource){
        resources.add(resource);
    }
    fun removeResource(resource: Resource) {
        resources.remove(resource)
    }
    fun updateVisibility(visibility: MemosVisibility) {
        this.visibility = visibility
    }
    fun setNewMemo(memo: Memo?=null){
        if(memo!=null){
            editId=memo.id;
            this.content = memo.content
            this.resources.apply {
                clear()
                addAll(memo.resourceList)
            }
            this.visibility = memo.visibility
        }else{
            editId=0;
            this.content="";
            this.resources.clear()
        }
    }

}

@Composable
fun rememberEditMemoState(initMemo:Memo,host:String, callback:EditMemoCallback=EditMemoCallback.DEFAULT, scope: CoroutineScope = rememberCoroutineScope(),) :EditMemoState{
    val onCallback by rememberUpdatedState(callback)
    return remember(initMemo,host,scope) {EditMemoState(initMemo.content,initMemo.resourceList,initMemo.visibility,host,onCallback,scope) }
}
@Composable
fun rememberEditMemoState(initContent:String="", initResources:List<Resource> = listOf(),
                          initVisibility: MemosVisibility = MemosVisibility.PUBLIC,
                          scope: CoroutineScope = rememberCoroutineScope(),
                          host:String, callback:EditMemoCallback=EditMemoCallback.DEFAULT) :EditMemoState {
    val onCallback by rememberUpdatedState(callback)
    return remember(initContent,initResources,initVisibility,host,scope) {EditMemoState(initContent,initResources,initVisibility,host,onCallback,scope) }
}
