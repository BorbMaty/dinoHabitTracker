package com.dinoHabitTracker.app.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Létrehozunk egy DataStore-t az appnak (fájlnév: progr3ss_prefs)
private val Context.dataStore by preferencesDataStore(name = "progr3ss_prefs")

class TokenStore(private val context: Context) {

    private val ACCESS = stringPreferencesKey("access_token")
    private val REFRESH = stringPreferencesKey("refresh_token")

    // Kiolvasható flow-k (null, ha nincs elmentve)
    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[REFRESH] }

    // Mentés / törlés (null esetén törli a kulcsot)
    suspend fun save(access: String?, refresh: String?) {
        context.dataStore.edit { prefs ->
            if (access == null) prefs.remove(ACCESS) else prefs[ACCESS] = access
            if (refresh == null) prefs.remove(REFRESH) else prefs[REFRESH] = refresh
        }
    }

    // Kényelmi függvények (nem kötelezők, de hasznosak)
    suspend fun clear() = save(null, null)
}