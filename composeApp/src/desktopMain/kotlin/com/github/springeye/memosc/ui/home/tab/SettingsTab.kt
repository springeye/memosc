package com.github.springeye.memosc.ui.home.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.springeye.memosc.components.CardItem
import com.github.springeye.memosc.components.ITextField
import com.github.springeye.memosc.model.MemosVisibility
import com.github.springeye.memosc.ui.app.AppScreenModel

object SettingsTab : Tab {
    @Composable
    override fun Content() {
        val model = getScreenModel<AppScreenModel>()
        val config by model.state.collectAsState()
        var host by remember {
            mutableStateOf("")
        }
        if (config is AppScreenModel.State.Result) {
            host = (config as AppScreenModel.State.Result).host;
        }
        var visibility by remember { mutableStateOf(MemosVisibility.PRIVATE) }
        Scaffold {
            CardItem(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(10.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(host, onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("API Server")
                        },
                        placeholder = {
                            Text("input server address")
                        })
                    Text("默认备忘录可见性", modifier = Modifier.padding(top = 15.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        RadioButton(visibility == MemosVisibility.PRIVATE, {
                            visibility = MemosVisibility.PRIVATE
                        })
                        Text("私有")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        RadioButton(visibility == MemosVisibility.PROTECTED, {
                            visibility = MemosVisibility.PROTECTED
                        })
                        Text("工作区")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        RadioButton(visibility == MemosVisibility.PUBLIC,{
                            visibility = MemosVisibility.PUBLIC
                        })
                        Text("公开")
                    }
                }
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "设置"
            val icon = rememberVectorPainter(Icons.Outlined.Settings)

            return remember {
                TabOptions(
                    index = 6u,
                    title = title,
                    icon = icon
                )
            }
        }

}