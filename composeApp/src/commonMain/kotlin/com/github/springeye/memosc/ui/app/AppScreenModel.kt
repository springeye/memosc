package com.github.springeye.memosc.ui.app
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.springeye.memosc.AppPreferences
import com.github.springeye.memosc.MemosApi
import com.github.springeye.memosc.getPlatform
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.printStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

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
            checkUpdate()
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
    suspend fun checkUpdate(){
        runCatching {
            val appVersion=getPlatform().versionCode
            httpClient.get("https://api.githhub.com/repos/springeye/memosc/releases/latest"){
                header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                header("Accept","application/vnd.github+json")
                header("X-GitHub-Api-Version","2022-11-28")


            }.bodyAsText()

        }.onSuccess {
            println(it)
        }.onFailure {
            it.printStack()
        }

    }
}