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
    private val credentials = HashMap<String, String>()
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url()
        val server = url.host()
        var username = url.username()
        var password = url.password()

        getBasicAuthInteractor.get(server.toString())?.let {
            username = it.userName
            password = it.password
        } ?: run {            
            val credentials = BasicAuth(
                server,
                username,
                password
            )
            saveBasicAuthInteractor.save(credentials)
        }

        if (!username.isNullOrEmpty()) {
            credentials[url.host()] = Credentials.basic(username, password)
            request = request.newBuilder().url(
                url.newBuilder().username("").password("").build()
            ).build()
        }
        credentials[url.host()]?.let {
            request = request.newBuilder().header("Authorization", it).build()
        }
        return chain.proceed(request)
    }
}
