package com.github.springeye.memosc.ui.app
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.springeye.memosc.AppPreferences
import com.github.springeye.memosc.MemosApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.cookies
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppScreenModel(private val api: MemosApi,
                     private val httpClient: HttpClient,
                     private val prefers: AppPreferences

) : StateScreenModel<AppScreenModel.State>(State.Loading) {
    sealed class State {

        object Loading : State()
        data class Result(val isLogin:Boolean) : State()
    }

    fun check(){

        screenModelScope.launch {
            val host=prefers.host()
            val username=prefers.username()
            val password=prefers.password()
            mutableState.value=State.Loading
            delay(1000)
            if(host!=null){
                val cookies=httpClient.cookies(host)
                if(cookies.isNotEmpty()){
                    mutableState.value=State.Result(true)
                    return@launch
                }
            }
            mutableState.value=State.Result(false)
//                println("cookies===>${cookies}")
        }
    }
}