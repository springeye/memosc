package com.github.springeye.memosc

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import java.io.File

actual fun dataStorePreferences(
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
    coroutineScope: CoroutineScope,
    migrations: List<DataMigration<Preferences>>
): DataStore<Preferences> = createDataStoreWithDefaults(
    corruptionHandler = corruptionHandler,
    migrations = migrations,
    coroutineScope = coroutineScope,
    path = { "${getPath()}/${SETTINGS_PREFERENCES}.preferences_pb" }
)
fun getPath():String{
    val file = File(System.getProperty("user.home"), ".memosc")
    if(!file.exists())file.mkdirs()
    return file.toString()
}