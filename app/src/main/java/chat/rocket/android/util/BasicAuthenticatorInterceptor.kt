package chat.rocket.android.util

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Credentials
import java.io.IOException
import timber.log.Timber
import chat.rocket.android.server.domain.model.BasicAuth
import chat.rocket.android.server.domain.GetBasicAuthInteractor
import chat.rocket.android.server.domain.SaveBasicAuthInteractor

import javax.inject.Inject

/**
 * An OkHttp interceptor which adds Authorization header based on URI userInfo
 * part. Can be applied as an
 * [application interceptor][OkHttpClient.interceptors]
 * or as a [ ][OkHttpClient.networkInterceptors].
 */
class BasicAuthenticatorInterceptor @Inject constructor (
    private val getBasicAuthInteractor: GetBasicAuthInteractor,
    private val saveBasicAuthInteractor: SaveBasicAuthInteractor
): Interceptor {
    private val credentialsHash = HashMap<String, String>()

    private fun saveCredentials(server: String, basicCredentials: String) {
        saveBasicAuthInteractor.save(
            BasicAuth(
                server,
                basicCredentials
            )
        )
    }

    private fun buildHash(server: String, basicCredentials: String) {
        credentialsHash[server] = basicCredentials
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url()
        val server = url.host()
        var username = url.username()

        if (!username.isNullOrEmpty()) {
            val credentialsBasic = Credentials.basic(username, url.password())
            saveCredentials(server, credentialsBasic)
            buildHash(server, credentialsBasic)
            request = request.newBuilder().url(
                url.newBuilder().username("").password("").build()
            ).build()
        }

        credentialsHash[server]?.let {
            request = request.newBuilder().header("Authorization", it).build()
        } ?: run {
            getBasicAuthInteractor.get(server.toString())?.let {
                buildHash(server, it.credentialsBasic)
                request = request.newBuilder().header("Authorization", it.credentialsBasic).build()
            }
        }

        return chain.proceed(request)
    }
}
