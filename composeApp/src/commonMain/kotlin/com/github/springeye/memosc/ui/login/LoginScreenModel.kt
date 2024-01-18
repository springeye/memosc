package com.github.springeye.memosc.ui.login


import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.springeye.memosc.AppPreferences
import com.github.springeye.memosc.MemosApi
import com.github.springeye.memosc.SignInInput
import io.ktor.client.HttpClient
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch

class LoginScreenModel(private val api: MemosApi,
                       private val client: HttpClient,
                       private val prefers: AppPreferences
): StateScreenModel<LoginScreenModel.State>(
    State.Init
) {
    sealed class State {
        object Init : State()
        object Loading : State()
        data class Result(val isSuccess:Boolean) : State()
    }
     fun login(host:String,username:String,password:String){
        screenModelScope.launch {
            mutableState.value = State.Loading
            prefers.host(host)
            val input = SignInInput(email = "", username = username, password = password)




//            client.post("api/v1/auth/signin"){
//                contentType(ContentType.Application.Json)
//                setBody(input)
//            }
            val res=api.signIn(input)
            //todo handle login success
            if(res.status.isSuccess()) {
                prefers.host(host)
                prefers.username(username)
                prefers.password(password)
                mutableState.value = State.Result(true)
            }else{
                println(res.toString())
                mutableState.value = State.Result(false)
            }
        }
        }
}