package com.github.springeye.memosc.model


import com.github.springeye.memosc.db.model.QueryQueries
import kotlinx.serialization.Serializable

@Serializable
enum class MemosVisibility {

    PUBLIC,

    PROTECTED,

    PRIVATE
}
@Serializable
enum class MemosRowStatus {

    NORMAL,

    ARCHIVED
}
@Serializable
data class Memo(
    val id: Long,
    val createdTs: Long,
    val creatorId: Long,
    val creatorName: String? = null,
    var content: String,
    var pinned: Boolean,
    val rowStatus: MemosRowStatus = MemosRowStatus.NORMAL,
    val updatedTs: Long,
    val visibility: MemosVisibility = MemosVisibility.PRIVATE,
    val resourceList: MutableList<Resource> = mutableListOf()
){
    fun loadResource(memoQueries: QueryQueries) {
        resourceList.clear()
        resourceList.addAll(memoQueries.selectResourceByMemoId(this.id).executeAsList().map { Resource.fromDBResource(it) })

    }
    fun toDbMemo(): com.github.springeye.memosc.db.model.Memo {
        return com.github.springeye.memosc.db.model.Memo(
            id,
            createdTs,
            creatorId,
            creatorName,
            content,
            pinned,
            rowStatus,
            visibility,
            updatedTs
        )
    }
    companion object{
        fun empty(): Memo {
            return Memo(
                System.currentTimeMillis(), System.currentTimeMillis(), 1, "henjue",
                "aaa",
                false, MemosRowStatus.NORMAL,System.currentTimeMillis(),
            )
        }
        fun fromDbMemo(memo:com.github.springeye.memosc.db.model.Memo):Memo{
            return with(memo) {
                Memo(
                    id,
                    createdTs,
                    creatorId,
                    creatorName,
                    content,
                    pinned,
                    rowStatus,
                    updatedTs,
                    visibility
                )
            }
        }
    }
}