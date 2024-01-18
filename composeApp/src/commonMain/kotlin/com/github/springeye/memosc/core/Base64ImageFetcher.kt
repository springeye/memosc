package com.github.springeye.memosc.core

import io.kamel.core.DataSource
import io.kamel.core.Resource
import io.kamel.core.config.ResourceConfig
import io.kamel.core.fetcher.Fetcher
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.reflect.KClass

data class Base64Image(val content:String?=null)
class Base64ImageFetcher : Fetcher<Base64Image> {

    override val inputDataKClass: KClass<Base64Image>
        get() = Base64Image::class
    override val source: DataSource
        get() = DataSource.None

    @OptIn(ExperimentalEncodingApi::class)
    override fun fetch(
        data: Base64Image,
        resourceConfig: ResourceConfig
    ): Flow<Resource<ByteReadChannel>> {
        return flow {
            try {
                val reg = "^data:image/([a-zA-Z]*);base64,([^\"]*)".toRegex(RegexOption.IGNORE_CASE)
                val matchResult =
                    reg.matchEntire(data.content ?: "") ?: throw Exception("not match")
                val (type, base64) = matchResult.destructured
                val bytes = Base64.decode(base64)
                emit(Resource.Success(ByteReadChannel(bytes)))
            } catch (e: Exception) {
                emit(Resource.Failure(e))
            }
        }
    }

    override val Base64Image.isSupported: Boolean
        get() = "^data:image/([a-zA-Z]*);base64,([^\"]*)".toRegex(RegexOption.IGNORE_CASE).matches(content?:"")
}