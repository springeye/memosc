package com.github.springeye.memosc.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import app.cash.paging.RemoteMediator
import com.github.springeye.memosc.db.model.QueryQueries

import com.github.springeye.memosc.model.Memo
import com.github.springeye.memosc.repository.MemoRepository


@OptIn(ExperimentalPagingApi::class)
class MemoRemoteMediator(private val api: MemoRepository,
                         private  val memoQueries: QueryQueries,
                         private val query: MemoQueryWhere = MemoQueryWhere(),): RemoteMediator<Int, Memo>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Memo>): MediatorResult {
        return try {
//            val loadKey = when (loadType) {
//                LoadType.REFRESH -> null
//
//                LoadType.PREPEND -> return MediatorResult.Success(
//                    endOfPaginationReached = true
//                )
//                LoadType.APPEND -> {
//                    val remoteKey =
//                        remoteKeyDao.remoteKeyByQuery(query?.creatorId,query?.rowStatus,query?.visibility)
//
//                        return MediatorResult.Success(
//                            endOfPaginationReached = true
//                        )
//
//                     remoteKey
//                }
//            }


            // Suspending network load via Retrofit. This doesn't need to be wrapped in a
            // withContext(Dispatcher.IO) { ... } block since Retrofit's Coroutine CallAdapter
            // dispatches on a worker thread.
            val response = api.listMemo(filter = query)
            memoQueries.transaction {
                memoQueries.deleteAllMemo()
                memoQueries.deleteAllResource()
                for (memo in response) {
                    memoQueries.insertMemo(memo.toDbMemo())
                    for (resource in memo.resourceList) {
                        memoQueries.insertResource(resource.toDBResource(memo.id))
                    }
                }
            }
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }

    }
}