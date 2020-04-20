package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.User

interface UserDataService {
    suspend fun getUserById(id: String): User?
    suspend fun getUserByExtId(extId: String): User?
    suspend fun createUser(user: User)
    suspend fun updateTokens(user: User)
}