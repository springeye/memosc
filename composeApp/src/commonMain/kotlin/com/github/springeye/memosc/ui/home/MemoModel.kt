package com.github.springeye.memosc.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import app.cash.paging.Pager
import app.cash.sqldelight.Query
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.springeye.memosc.CreateMemoInput
import com.github.springeye.memosc.MemosApi
import com.github.springeye.memosc.PatchMemoInput
import com.github.springeye.memosc.UpdateMemoOrganizerInput
import com.github.springeye.memosc.core.createIFile
import com.github.springeye.memosc.core.formatDate
import com.github.springeye.memosc.core.parseDate
import com.github.springeye.memosc.db.MemoQueryWhere
import com.github.springeye.memosc.db.model.QueryQueries
import com.github.springeye.memosc.model.DailyUsageStat
import com.github.springeye.memosc.model.Memo
import com.github.springeye.memosc.model.MemosRowStatus
import com.github.springeye.memosc.model.MemosVisibility
import com.github.springeye.memosc.model.Resource
import com.github.springeye.memosc.model.User
import com.github.springeye.memosc.model.calculateMatrix
import com.github.springeye.memosc.model.initialMatrix
import com.github.springeye.memosc.repository.MemoPagingSource
import com.github.springeye.memosc.repository.MemoRepository
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Date

@OptIn(ExperimentalPagingApi::class)
class MemoModel(
    private val api: MemosApi,
    private val repository: MemoRepository,
    private val memoQueries: QueryQueries,


    ) : ScreenModel {


    private val resources =  mutableStateListOf<Resource>()
    var resourcesGroup: Flow<Map<Instant, List<Resource>>> = snapshotFlow { resources.toList() }
       .map {
            return@map it.sortedByDescending { it.createdTs }.groupBy {
                val old = Instant.fromEpochSeconds(it.createdTs).formatDate("yyyy-MM")
                val time = old.parseDate("yyyy-MM")
                val date = Instant.fromEpochMilliseconds(time)
                date
            }
        }

    private val _query = MutableStateFlow(MemoQueryWhere())

    @OptIn(ExperimentalCoroutinesApi::class)
    val pager = _query.flatMapLatest { query ->
        Pager(
            config = PagingConfig(pageSize = 10),
//            remoteMediator = MemoRemoteMediator(repository, memoQueries,query)
        ) {
            MemoPagingSource(repository, query)
//            QueryPagingSource(
//                countQuery =memoQueries.countMemos(),
//                transacter =memoQueries,
//                context = Dispatchers.IO,
//                queryProvider = createQuery(memoQueries,query)
//            )
        }.flow.cachedIn(screenModelScope)
    }
    var user by mutableStateOf<User?>(null)
        private set

    fun fetchExtraInfo() {
        screenModelScope.launch {
            user = api.me()
            if (user != null) {
                resources.clear()
                resources.addAll(api.getResources())


                val stats = api.stats(user!!.id).map {
                    Date(it * 1000).toInstant().toKotlinInstant().toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    ).date
                }.groupBy { it }
                matrix=calculateMatrix(stats)

            }
        }

    }

    var matrix by mutableStateOf<List<DailyUsageStat>>(initialMatrix())
        private set


    var query by mutableStateOf(MemoQueryWhere())



    suspend fun upload(path: String): Resource {
        val multipart = formData {
            val iFile = createIFile(path)
            val bytes = iFile.readBytes()
            val mimeType = iFile.mimeType
            val filename = iFile.filename
            append("file", bytes, Headers.build {
                append(HttpHeaders.ContentType, mimeType)
                append(HttpHeaders.ContentDisposition, "filename=${filename}")
            })
        }
        return api.uploadResource(MultiPartFormDataContent(multipart))

    }


    suspend fun submit(
        editId: Long,
        content: String,
        resources: List<Resource>,
        visibility: MemosVisibility
    ) {

        if (editId > 0) {
            val memo = api.patchMemo(
                editId,
                PatchMemoInput(
                    editId,
                    content = content,
                    visibility = visibility,
                    resourceIdList = resources.map { it.id })
            );
//                memos.indexOfFirst { memo.id == it.id }.takeIf { it >= 0 }?.let {
//                    memos[it] = memo
//                }
        } else {
            val memo = api.createMemo(
                CreateMemoInput(
                    content,
                    visibility = visibility,
                    resourceIdList = resources.map { it.id })
            );
//                memos.add(0, memo)
        }

    }


    suspend fun filterCreatorId(creatorId: Long? = null) {
        query = query.copy(creatorId = creatorId)
        _query.emit(_query.value.copy(creatorId = creatorId))
    }

    suspend fun filterRowStatus(rowStatus: MemosRowStatus? = null) {
        query = query.copy(rowStatus = rowStatus)
        _query.emit(_query.value.copy(rowStatus = rowStatus))
    }

    suspend fun filterVisibility(visibility: MemosVisibility? = null) {
        query = query.copy(visibility = visibility)
        _query.emit(_query.value.copy(visibility = visibility))
    }

    fun filterContent(content: String? = null) {
        query = query.copy(content = content)
        screenModelScope.launch {
            _query.emit(_query.value.copy(content = content))
        }
    }


    private fun createQuery(
        memoQueries: QueryQueries,
        query: MemoQueryWhere
    ): (limit: Long, offset: Long) -> Query<Memo> {
        return { limit: Long, offset: Long ->
            memoQueries.memos(
                limit,
                offset
            ) { id: Long, createdTs: Long, creatorId: Long, creatorName: String?, content: String, pinned: Boolean, rowStatus: MemosRowStatus, visibility: MemosVisibility, updatedTs: Long ->
                return@memos com.github.springeye.memosc.model.Memo(
                    id,
                    createdTs,
                    creatorId,
                    creatorName,
                    content,
                    pinned,
                    rowStatus,
                    updatedTs,
                    visibility
                ).apply {
                    loadResource(memoQueries)
                }
            }
        }
    }

    suspend fun setPininned(it: Memo) {
        api.updateMemoOrganizer(it.id, UpdateMemoOrganizerInput(!it.pinned))
    }

    suspend fun remove(it: Memo) {
        api.deleteMemo(it.id)
    }
}
