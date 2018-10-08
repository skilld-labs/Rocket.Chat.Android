package chat.rocket.android.util

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Credentials
import java.io.IOException
import timber.log.Timber

import chat.rocket.android.server.domain.SaveBasicAuthInteractor
import chat.rocket.android.server.domain.GetBasicAuthInteractor

/**
 * An OkHttp interceptor which adds Authorization header based on URI userInfo
 * part. Can be applied as an
 * [application interceptor][OkHttpClient.interceptors]
 * or as a [ ][OkHttpClient.networkInterceptors].
 */
class BasicAuthenticatorInterceptor : Interceptor {
    private val credentials = HashMap<String, String>()
    
    private var saveBasicAuthInteractor: SaveBasicAuthInteractor
    private var getBasicAuthInteractor: GetBasicAuthInteractor

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        Timber.d("INTERCEPT BASIC AUTH")
        var request = chain.request()
        val url = request.url()
        val username = url.username()
        getBasicAuthInteractor.get()?.let {
            Timber.d("BASIC AUTH REPO GOT THIS ${it}")
        } ?: run {
            Timber.d("We will store this data ${username}")
            saveBasicAuthInteractor.save(username)
        }
        if (!username.isNullOrEmpty()) {
            credentials[url.host()] = Credentials.basic(username, url.password())
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
