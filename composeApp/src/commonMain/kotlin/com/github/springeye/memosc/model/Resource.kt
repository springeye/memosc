package com.github.springeye.memosc.model

import io.ktor.http.URLBuilder
import io.ktor.http.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Resource(
    val id: Long,
    val createdTs: Long,
    val creatorId: Long,
    val filename: String,
    val size: Long,
    val type: String,
    val updatedTs: Long,
    val externalLink: String?,
    val publicId: String?,
    @Transient
    val memoId: Long=0
) {
    fun toDBResource( memoId: Long): com.github.springeye.memosc.db.model.Resource {
        return com.github.springeye.memosc.db.model.Resource(
            id,
            createdTs,
            creatorId,
            filename,
            size,
            type,
            updatedTs,
            externalLink,
            publicId,
            memoId
        )
    }
    fun uri(host: String): String {
        if (!externalLink.isNullOrEmpty()) {
            return URLBuilder(externalLink).buildString()
        }
        if (!publicId.isNullOrEmpty()) {
            return URLBuilder(host)
                .apply {
                    path("o","r",id.toString(),publicId)
                }
                .buildString()
        }
        return             return URLBuilder(host)
            .apply {
                path("o","r",id.toString(),filename)
            }
            .buildString()
    }
    companion object{
        fun fromDBResource(resource:com.github.springeye.memosc.db.model.Resource):Resource{
            return with(resource) {
                Resource(
                    id,
                    createdTs,
                    creatorId,
                    filename,
                    size,
                    type,
                    updatedTs,
                    externalLink,
                    publicId,
                    memoId?:0
                )
            }

        }
    }
}
