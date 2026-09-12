package com.bookcabin.tvpulse.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private val isFirstLaunchKey = booleanPreferencesKey("is_first_launch")

    val isFirstLaunchFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[isFirstLaunchKey] ?: true
        }

    suspend fun setFirstLaunch(isFirstLaunch: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isFirstLaunchKey] = isFirstLaunch
        }
    }
}
