package chat.rocket.android.server.domain

interface BasicAuthRepository {
    fun save(credentials: String)
    fun get(): String?
}