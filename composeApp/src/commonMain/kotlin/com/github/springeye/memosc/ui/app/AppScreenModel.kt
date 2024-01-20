package com.github.springeye.memosc.ui.app
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.springeye.memosc.AppPreferences
import com.github.springeye.memosc.api.MemosApi
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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class AppScreenModel(private val api: MemosApi,
                     private val httpClient: HttpClient,
                     private val prefers: AppPreferences

) : StateScreenModel<AppScreenModel.State>(State.Loading) {
    sealed class State {

        object Loading : State()
        data class Result(val isLogin:Boolean,val host:String) : State()
    }

    fun check(){
        screenModelScope.launch {
            checkUpdate()
            val host=prefers.host()?:""
            val username=prefers.username()
            val password=prefers.password()
            mutableState.value=State.Loading
            delay(1000)
            if(host!=null){
                val cookies=httpClient.cookies(host)
                if(cookies.isNotEmpty()){
                    mutableState.value=State.Result(true,host)
                    return@launch
                }
            }
            mutableState.value=State.Result(false,host)
//                println("cookies===>${cookies}")
        }
    }
    var newVersion by mutableStateOf<Pair<String,String>?>(null)
        private set
    suspend fun checkUpdate(){
        val appVersion=getPlatform().versionCode
        runCatching {
            httpClient.get("https://api.github.com/repos/springeye/memosc/releases/latest"){
                header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                header("Accept","application/vnd.github+json")
                header("X-GitHub-Api-Version","2022-11-28")


            }.body<JsonObject>()

        }.onSuccess {

            val latestVersion = (it.get("tag_name") as? JsonPrimitive)?.content?:""
            val url = (it["html_url"] as? JsonPrimitive)?.content
//            val latestVersion="1.0.1"
//            val url ="https://github.com/springeye/memosc/releases/tag/1.0.0"
            if((latestVersion.compareTo(appVersion) ?: 0) > 0){
                url?.let {
                    newVersion=latestVersion to it
                }
            }else{
            }

        }.onFailure {
            it.printStack()
        }

    }
}