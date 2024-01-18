package com.github.springeye.memosc.ui.home

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.cash.paging.Pager
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.springeye.memosc.api.MemosApi
import com.github.springeye.memosc.api.UpdateMemoOrganizerInput
import com.github.springeye.memosc.db.MemoQueryWhere
import com.github.springeye.memosc.db.model.QueryQueries
import com.github.springeye.memosc.model.Memo
import com.github.springeye.memosc.repository.MemoPagingSource
import com.github.springeye.memosc.repository.MemoRepository
import kotlinx.coroutines.flow.Flow

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
