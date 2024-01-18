package com.github.springeye.memosc.db

import com.github.springeye.memosc.db.model.AppDatabase
import com.github.springeye.memosc.db.model.MemoQueries
import com.github.springeye.memosc.model.Memo

actual fun createMemoDao(database: AppDatabase): MemoDao {
    return AndroidMemoDao(database)
}
internal class AndroidMemoDao(private val database: AppDatabase): MemoDao {

    override suspend fun findAll(): List<Memo> {
        return database.memoQueries.selectAll().executeAsList().map {
            Memo.fromDbMemo(it)
        }
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }

    override suspend fun findById(memoId: Long): Memo? {
        TODO("Not yet implemented")
    }

    override suspend fun insertAll(vararg memos: Memo, clear: Boolean) {
        database.memoQueries.transaction {
            if(clear){
                database.memoQueries.deleteAll()
            }
            for (memo in memos) {
                val item= with(memo) {
                    com.github.springeye.memosc.db.model.Memo(
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
                database.memoQueries.insertMemo(item)
            }
        }
    }

    override fun memoQueries(): MemoQueries {
        return database.memoQueries
    }
}