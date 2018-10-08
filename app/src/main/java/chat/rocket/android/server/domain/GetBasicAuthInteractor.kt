package chat.rocket.android.server.domain

import javax.inject.Inject

class GetBasicAuthInteractor @Inject constructor(private val repository: BasicAuthRepository) {
    fun get(): String? = repository.get()
}