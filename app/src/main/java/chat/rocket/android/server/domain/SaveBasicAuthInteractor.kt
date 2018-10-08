package chat.rocket.android.server.domain

import javax.inject.Inject

class SaveBasicAuthInteractor @Inject constructor(private val repository: BasicAuthRepository) {
    fun save(credentials: String) = repository.save(credentials)
}