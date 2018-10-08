package chat.rocket.android.server.domain

import javax.inject.Inject

class GetBasicAuthInteractor @Inject constructor(val repository: BasicAuthRepository) {
    fun get(url: String) = repository.load().firstOrNull { basicAuth ->
        url == basicAuth.serverUrl
    }
}