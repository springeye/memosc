package com.github.springeye.memosc.db

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.logs.LogSqliteDriver
import com.github.springeye.memosc.db.model.AppDatabase
import io.github.aakira.napier.Napier

actual fun createAppDatabase(): AppDatabase {
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:test.db").run {
        LogSqliteDriver(this as SqlDriver){
            Napier.d(it)
        }
    }
    AppDatabase.Schema.create(driver)
    return AppDatabase(driver,com.github.springeye.memosc.db.model.Memo.Adapter(
        rowStatusAdapter = EnumColumnAdapter(),
        visibilityAdapter = EnumColumnAdapter()
    ),com.github.springeye.memosc.db.model.RemoteKey.Adapter(
        rowStatusAdapter = EnumColumnAdapter(),
        visibilityAdapter = EnumColumnAdapter()
    ));
}