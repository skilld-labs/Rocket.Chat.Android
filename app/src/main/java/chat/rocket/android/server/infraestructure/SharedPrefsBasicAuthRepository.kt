package chat.rocket.android.server.infraestructure

import android.content.SharedPreferences
import androidx.core.content.edit
import chat.rocket.android.server.domain.BasicAuthRepository
import chat.rocket.android.server.domain.model.BasicAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

private const val CREDENTIALS_KEY = "CREDENTIALS_KEY"

class SharedPrefsBasicAuthRepository(
    private val preferences: SharedPreferences,
    private val moshi: Moshi
) : BasicAuthRepository {

    override fun save(credentials: BasicAuth) {
        val basicAuths = load()

        val newList = basicAuths.filter { basicAuth -> credentials. serverUrl != basicAuth.serverUrl }
            .toMutableList()
        newList.add(0, credentials)
        save(newList)
    }

    override fun load(): List<BasicAuth> {
        val json = preferences.getString(CREDENTIALS_KEY, "[]")
        val type = Types.newParameterizedType(List::class.java, BasicAuth::class.java)
        val adapter = moshi.adapter<List<BasicAuth>>(type)

        return adapter.fromJson(json) ?: emptyList()
    }

    private fun save(credentials: List<BasicAuth>) {
        val type = Types.newParameterizedType(List::class.java, BasicAuth::class.java)
        val adapter = moshi.adapter<List<BasicAuth>>(type)
        preferences.edit {
            putString(CREDENTIALS_KEY, adapter.toJson(credentials))
        }
    }
}