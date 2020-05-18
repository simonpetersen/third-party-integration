package dtu.openhealth.integration.shared.service.data.usertoken

import dtu.openhealth.integration.shared.model.UserToken

interface IUserTokenDataService {
    suspend fun getUserById(id: String): UserToken?
    suspend fun getUserByExtId(extId: String): UserToken?
    fun insertUser(userToken: UserToken)
    suspend fun updateTokens(userToken: UserToken)
    suspend fun getTokensFromThirdParty(thirdParty: String): List<UserToken>
    fun getUserIdByExtId(extId: String, callback: (String) -> Unit)
 }