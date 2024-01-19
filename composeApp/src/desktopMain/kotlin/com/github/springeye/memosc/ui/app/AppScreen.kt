package com.github.springeye.memosc.ui.app
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.springeye.memosc.LoadingAnimation
import com.github.springeye.memosc.LocalNotification
import com.github.springeye.memosc.ui.home.HomeScreen
import com.github.springeye.memosc.ui.user.LoginScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi

object AppScreen : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
//        val screenModel = rememberScreenModel { AppScreenModel() }
        val no= LocalNotification.current
        val screenModel = getScreenModel<AppScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        LaunchedEffect(state){
            println(state)
            when (state) {
                is AppScreenModel.State.Loading->{
//                    no.showLoading()
                }
                is AppScreenModel.State.Result->{
//                    no.hideLoading()
                    if((state as AppScreenModel.State.Result).isLogin){
                        navigator.replace(HomeScreen)
                    }else{
                        navigator.replace(LoginScreen)
                    }
                }

                else -> {

                }
            }
        }

        MaterialTheme {

            Column(Modifier.fillMaxWidth().fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
                ) {
                if(state is AppScreenModel.State.Loading) {
                    LoadingAnimation()
                }
            }
        }
        LaunchedEffect(Unit){
            screenModel.check()
        }
    }
}