package com.github.springeye.memosc.repository

import androidx.paging.PagingState
import app.cash.paging.PagingSource
import com.github.springeye.memosc.db.MemoQueryWhere
import com.github.springeye.memosc.model.Memo
import io.github.aakira.napier.Napier

class MemoPagingSource(
    val repository: MemoRepository,
    val filter: MemoQueryWhere,
    ): PagingSource<Int, Memo>() {
    override fun getRefreshKey(state: PagingState<Int, Memo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Memo> {
        val nextPage:Int = (params.key ?: 0)
        val size = params.loadSize
        Napier.d("page:$nextPage size:$size")
        val newFilter=filter.copy(offset = nextPage * params.loadSize, limit = size)
        val list = repository.listMemo(filter = newFilter)
        return LoadResult.Page(list,
            prevKey = if(nextPage>0) nextPage-1 else null,
            nextKey =  if(list.size>=size) nextPage+1 else null)
    }

}