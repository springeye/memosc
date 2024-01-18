package com.github.springeye.memosc.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import app.cash.paging.Pager
import app.cash.paging.map
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
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
import com.github.springeye.memosc.model.calculateMatrix
import com.github.springeye.memosc.model.initialMatrix
import com.github.springeye.memosc.repository.MemoPagingSource
import com.github.springeye.memosc.repository.MemoRepository
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.LoadState

@OptIn(ExperimentalPagingApi::class)
class ProfileModel(
    private val api: MemosApi,
    private val repository: MemoRepository,
    private val memoQueries: QueryQueries,


    ) : ScreenModel {




    fun getPagerByUserId(uid:Long): Flow<PagingData<Memo>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
//            remoteMediator = MemoRemoteMediator(repository, memoQueries,query)
        ) {
            MemoPagingSource(repository,MemoQueryWhere(creatorId = uid))

        }.flow.cachedIn(screenModelScope)
    }



    suspend fun setPininned(it: Memo) {
        api.updateMemoOrganizer(it.id, UpdateMemoOrganizerInput(!it.pinned))
    }

    suspend fun remove(it: Memo) {
        api.deleteMemo(it.id)
    }
}
