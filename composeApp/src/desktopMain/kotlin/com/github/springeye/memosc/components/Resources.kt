package com.github.springeye.memosc.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import com.github.springeye.memosc.model.Memo
import com.github.springeye.memosc.model.Resource
import com.github.springeye.memosc.ui.AppModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.math.ceil
import kotlin.math.min

@Composable
fun MemoResourceContent(memo: Memo,host:String) {
    memo.resourceList?.let { resourceList ->
        val cols = min(3, resourceList.size)
        val imageList = resourceList.filter { it.type.startsWith("image/") }
        if (imageList.isNotEmpty()) {
            val rows = ceil(imageList.size.toFloat() / cols).toInt()
            for (rowIndex in 0 until rows) {
                Row {
                    for (colIndex in 0 until cols) {
                        val index = rowIndex * cols + colIndex
                        if (index < imageList.size) {
                            Box(modifier = Modifier.fillMaxWidth(1f / (cols - colIndex))) {
                                val uri = imageList[index].uri(host)
                                KamelImage(
                                    asyncPainterResource(uri),
                                    onLoading = {
                                        Text("加载中")
                                    },
                                    onFailure = {
                                        it.printStackTrace()
                                        Text("加载失败")
                                    },
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.fillMaxWidth(1f / cols))
                        }
                    }
                }
            }
        }
        resourceList.filterNot { it.type.startsWith("image/") }.forEach { resource ->
            Attachment(resource,host)
        }
    }
}

@Composable
fun Attachment(
    resource: Resource,
    host:String
) {
    val uriHandler = LocalUriHandler.current

    AssistChip(
        modifier = Modifier.padding(bottom = 10.dp),
        onClick = {
            uriHandler.openUri(resource.uri(host))
        },
        label = { Text(resource.filename) },
        leadingIcon = {
            Icon(
                Icons.Outlined.Attachment,
                contentDescription = "附件",
                Modifier.size(AssistChipDefaults.IconSize)
            )
        }
    )
}