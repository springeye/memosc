package com.github.springeye.memosc.repository

import com.github.springeye.memosc.api.MemosApi
import com.github.springeye.memosc.db.MemoQueryWhere
import com.github.springeye.memosc.model.Memo
import kotlinx.coroutines.delay


class MemoRepository(private val api: MemosApi,) {
    suspend fun  listMemo(filter: MemoQueryWhere=MemoQueryWhere()): List<Memo> {
        return api.listMemo(filter.creatorId, filter.creatorUsername, filter.pinned, filter.tag, filter.limit, filter.offset, filter.rowStatus, filter.visibility, filter.content)
    }
}
