package com.github.springeye.memosc.ui
import cafe.adriel.voyager.core.model.StateScreenModel
import com.github.springeye.memosc.AppSettings

class AppModel(private val host:String): StateScreenModel<AppSettings>(AppSettings(host)) {
    fun setHost(host: String){
        mutableState.value=mutableState.value.copy(host=host)
    }
}