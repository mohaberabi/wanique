package com.mohaberabi.core.data.auth

import android.content.SharedPreferences
import com.mohaberabi.core.domain.session.AuthInfo
import com.mohaberabi.core.domain.session.SessionStorage
import io.ktor.util.Identity.decode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EncryptedSessionStorage(
    private val prefs: SharedPreferences
) : SessionStorage {
    override suspend fun get(): AuthInfo? {

        return withContext(Dispatchers.IO) {

            val json = prefs.getString(KEY_AUTH_INFO, null)

            json?.let {
                Json.decodeFromString<AuthInfoSerializable>(json).toAuthInfo()
                
            }
        }
    }

    override suspend fun set(
        info:
        AuthInfo?
    ) {
        withContext(Dispatchers.IO) {

            if (info == null) {
                prefs.edit().remove(KEY_AUTH_INFO).commit()

                return@withContext
            } else {
                val json = Json.encodeToString(info.toAuthInfoSerializable())
                prefs.edit().putString(KEY_AUTH_INFO, json).commit()
            }
        }
    }


    companion object {
        const val KEY_AUTH_INFO = "KEY_AUTH_INFO"
    }
}