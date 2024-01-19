package com.github.springeye.memosc.ui.user

import com.github.springeye.memosc.LocalNotification
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.springeye.memosc.ui.home.HomeScreen
import com.github.springeye.memosc.ui.login.LoginScreenModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

object LoginScreen:Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<LoginScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        var host:String by remember { mutableStateOf("") }
        var username:String by remember { mutableStateOf("") }
        var password:String by remember { mutableStateOf("") }
        var rememberPassword by remember { mutableStateOf(true) }
        val localNotification= LocalNotification.current
        LaunchedEffect(state){
            when (state) {

                is LoginScreenModel.State.Result ->{
                    localNotification.hideLoading()
                    if((state as LoginScreenModel.State.Result).isSuccess){
                        navigator.replace(HomeScreen)
                    }else{
                        localNotification.showPopUpMessage("登录失败")
//                        navigator.replace(LoginScreen)
                    }
                }
                is LoginScreenModel.State.Loading ->{
                    localNotification.showLoading("登录中")
                }

                else -> {

                }
            }
        }
            val focusManager = LocalFocusManager.current
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                Scaffold {

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(350.dp)) {
                            Row(Modifier.wrapContentSize(), verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painterResource("logo.png"),
                                    null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(50.dp)
                                        .clip(CircleShape)
                                    ,)
                                Text("Memos", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(start = 10.dp))
                            }
                            OutlinedTextField(host, onValueChange = {
                                host = it
                            }, label = {
                                Text("服务器")
                            },  singleLine = true,modifier = Modifier.fillMaxWidth().padding(top = 30.dp),

                                )
                            OutlinedTextField(username, onValueChange = {
                                username = it
                            }, label = {
                                Text("用户名")
                            }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 30.dp))
                            OutlinedTextField(password, onValueChange = {
                                password = it
                            }, label = {
                                Text("密码")
                            },  singleLine = true,modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                            Row(modifier = Modifier.align(Alignment.Start).padding(top = 10.dp)) {
                                Checkbox(
                                    rememberPassword,
                                    onCheckedChange = { rememberPassword = it },
                                )
                                Text("保持登录", modifier = Modifier.align(CenterVertically))
                            }
                            Button(onClick = {
                                screenModel.login(host,username,password)
                            }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)){
                                Text("登录")
                            }
                        }
                    }

                }
            }
    }
}
