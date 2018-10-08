package chat.rocket.android.server.infraestructure

import android.content.SharedPreferences
import chat.rocket.android.server.domain.BasicAuthRepository

class SharedPrefsBasicAuthRepository(private val preferences: SharedPreferences) : BasicAuthRepository {

    override fun save(credentials: String) {
        preferences.edit().putString(BASIC_AUTH_KEY, credentials).apply()
    }

    override fun get(): String? {
        return preferences.getString(BASIC_AUTH_KEY, null)
    }

    companion object {
        private const val BASIC_AUTH_KEY = "basic_auth"
    }
}