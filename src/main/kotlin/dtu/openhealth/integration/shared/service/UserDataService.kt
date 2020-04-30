package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.UserToken

interface UserDataService {
    suspend fun getUserById(id: String): UserToken?
    suspend fun getUserByExtId(extId: String): UserToken?
    fun insertUser(userToken: UserToken)
    suspend fun updateTokens(userToken: UserToken)
}