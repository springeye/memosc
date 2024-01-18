package com.github.springeye.memosc.di

import com.github.springeye.memosc.AppPreferences
import com.github.springeye.memosc.CoreComponent
import com.github.springeye.memosc.CoreComponentImpl
import com.github.springeye.memosc.api.MemosApi

import com.github.springeye.memosc.db.createAppDatabase
import com.github.springeye.memosc.db.model.AppDatabase
import com.github.springeye.memosc.repository.MemoRepository
import com.github.springeye.memosc.ui.AppModel
import com.github.springeye.memosc.ui.app.AppScreenModel
import com.github.springeye.memosc.ui.home.ArchivedModel
import com.github.springeye.memosc.ui.home.MemoModel
import com.github.springeye.memosc.ui.home.NotifiModel
import com.github.springeye.memosc.ui.home.ProfileModel
import com.github.springeye.memosc.ui.login.LoginScreenModel
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.builtin.CallConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module


@OptIn(ExperimentalSerializationApi::class)
val appModule = module {

    single<CoreComponent> {
        CoreComponentImpl()
    }
    single<AppPreferences> {
        get<CoreComponent>().appPreferences
    }

    single {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys=true
            explicitNulls=false
            allowSpecialFloatingPointValues = true
//            namingStrategy= JsonNamingStrategy.SnakeCase
            allowStructuredMapKeys = true
            prettyPrint = false
            useArrayPolymorphism = false
        }
    }
    single<CookiesStorage> {
        PersistentCookiesStorage(get())
    }
    single<HttpClient> {
        HttpClient {
            this.followRedirects=true
            defaultRequest {

                if(headers["Content-Type"].isNullOrEmpty()){
                    header("Content-Type","application/json")
                }
                url{
                    val prefer: AppPreferences =get()
                    val url=runBlocking { prefer.host() }
                    //this url should https://api.github.com
                    url
                }
            }
            install(Logging) {
                logger = object: Logger {
                    override fun log(message: String) {
//                        Napier.v("HTTP Client", null, message)
                    }
                }
                level = LogLevel.ALL
            }
            install(HttpCookies){
                this.storage=get()
            }
            install(ContentNegotiation) {
                json(json= get())
            }
            install(HttpRedirect)
        }
    }
    single {

        ktorfit {
            val sp=get<AppPreferences>()
            val host = runBlocking { sp.host() ?: "" }
            baseUrl(host,false)
            httpClient(get<HttpClient>())
            converterFactories(
                CallConverterFactory()
            )
        }
    }
    single {
            val ktorfit=get<Ktorfit>()
        ktorfit.create<MemosApi>()
    }
    single{
        MemoRepository(get())
    }
    single {
        createAppDatabase()
    }
    single {
        get<AppDatabase>().queryQueries
    }
}
val homeModule = module {

    single {
        val sp=get<AppPreferences>()
        val host = runBlocking { sp.host() ?: "" }
        AppModel(host)
    }
    single { AppScreenModel(get(), get(),get()) }
    single { LoginScreenModel(get(),get(),get()) }
    single { MemoModel(get(),get(),get()) }
    single { NotifiModel() }
    single { ProfileModel(get(),get(),get()) }
    single { ArchivedModel(get(),get(),get()) }

}

