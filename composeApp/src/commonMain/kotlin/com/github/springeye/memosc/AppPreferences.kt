package com.github.springeye.memosc
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

interface AppPreferences {
    suspend fun host(): String?
    suspend fun username(): String?
    suspend fun password(): String?
    suspend fun host(host:String)
    suspend fun username(username:String)
    suspend fun password(password:String)
    suspend fun setString(key:String,value:String)
    suspend fun getString(key:String):String?
}

internal class AppPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : AppPreferences {

    private companion object {
        private const val PREFS_TAG_KEY = "AppPreferences"
        private const val HOST = "host"
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
    }

    private val hostKey = stringPreferencesKey("$PREFS_TAG_KEY$HOST")
    private val usernameKey = stringPreferencesKey("$PREFS_TAG_KEY$USERNAME")
    private val passwordKey = stringPreferencesKey("$PREFS_TAG_KEY$PASSWORD")


    override suspend fun host(): String?  = dataStore.data.map { preferences ->
        preferences[hostKey]
    }.first()
    override suspend fun host(host: String) {
        dataStore.edit { preferences ->
            preferences[hostKey] = host
        }
    }

    override suspend fun username(): String? =dataStore.data.map { preferences ->
        preferences[usernameKey]
    }.first()

    override suspend fun username(username: String){
        dataStore.edit { preferences ->
            preferences[usernameKey] = username
        }
    }

    override suspend fun password(): String? =dataStore.data.map { preferences ->
        preferences[passwordKey]
    }.first()

    override suspend fun password(password: String) {
        dataStore.edit { preferences ->
            preferences[passwordKey] = password
        }
    }

    override suspend fun setString(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("$PREFS_TAG_KEY$key")]=value
        }
    }

    override suspend fun getString(key: String): String? = dataStore.data.map {preferences ->
        preferences[stringPreferencesKey("$PREFS_TAG_KEY$key")]
    }.firstOrNull()
}