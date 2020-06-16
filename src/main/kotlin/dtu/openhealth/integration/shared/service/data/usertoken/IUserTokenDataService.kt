package dtu.openhealth.integration.shared.service.data.usertoken

import dtu.openhealth.integration.shared.model.UserToken

interface IUserTokenDataService {
    suspend fun getUserById(id: String): UserToken?
    suspend fun getUserByExtId(extId: String): UserToken?
    fun insertUserToken(userToken: UserToken)
    fun updateTokens(userToken: UserToken)
    fun deleteTokensInList(userIdList: List<String>)
    suspend fun getTokensFromThirdParty(thirdParty: String): List<UserToken>
    fun getUserIdByExtId(extId: String, callback: (String) -> Unit)
    fun getTokensFromIdList(idList: List<String>, callback: (List<UserToken>) -> Unit)
 }