package com.github.springeye.memosc.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NotifiModel:ScreenModel {
    val items= mutableStateListOf("")
    var counter by mutableStateOf( 0)
    init {

            snapshotFlow { items.toList() }.onEach {
                println(it)
                counter+=it.sumOf { it.length };
            }.launchIn(screenModelScope)
    }
    fun  addItem(value:String){
        items.add(value)
    }
}