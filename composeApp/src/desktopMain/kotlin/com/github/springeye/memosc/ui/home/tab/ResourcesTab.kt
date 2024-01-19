package com.github.springeye.memosc.ui.home.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.MaterialTheme
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
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.springeye.memosc.components.CardItem
import com.github.springeye.memosc.core.formatDate
import com.github.springeye.memosc.ui.AppModel
import com.github.springeye.memosc.ui.home.MemoModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

object ResourcesTab : Tab {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<MemoModel>()
        val settings = getScreenModel<AppModel>().state.value
        val group by model.resourcesGroup.collectAsState(mapOf())
        CardItem(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(10.dp).fillMaxWidth()
        ) {
            Column {
                for (entry in group) {
                    Row {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 15.dp).padding(top = 10.dp)
                        ) {
                            Text(
                                "${entry.key.formatDate("yyyy")}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "${entry.key.formatDate("MM")}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        FlowRow() {
                            for (resource in entry.value) {
                                KamelImage(
                                    asyncPainterResource(resource.uri(settings.host)),
                                    "",
                                    modifier = Modifier.size(100.dp)
                                        .padding(5.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.FillWidth,

                                    )
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    override val options: TabOptions
        @Composable
        get() {
            val title = "资源库"
            val icon = rememberVectorPainter(Icons.Outlined.PhotoLibrary);
            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }

}