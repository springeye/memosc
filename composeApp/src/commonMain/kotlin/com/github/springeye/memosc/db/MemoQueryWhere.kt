package com.github.springeye.memosc.db

import com.github.springeye.memosc.model.MemosRowStatus
import com.github.springeye.memosc.model.MemosVisibility


data class MemoQueryWhere(
    val creatorId: Long?=null,
    val creatorUsername: String?=null,
    val pinned: Boolean?=null,
    val tag: String?=null,
    val limit: Int?=null,
    val offset: Int?=null,
    val rowStatus: MemosRowStatus?=null,
    val visibility: MemosVisibility?=null,
    val content: String?=null
)