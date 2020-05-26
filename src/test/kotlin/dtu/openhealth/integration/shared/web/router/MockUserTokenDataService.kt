package dtu.openhealth.integration.shared.web.router

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService

class MockUserTokenDataService(
        private val userTokens: MutableList<UserToken>
) : IUserTokenDataService {

    fun userTokens(): List<UserToken>
    {
        return userTokens
    }

    override suspend fun getUserById(id: String): UserToken? {
        TODO("Not yet implemented")
    }

    override suspend fun getUserByExtId(extId: String): UserToken? {
        TODO("Not yet implemented")
    }

    override fun insertUser(userToken: UserToken) {
        TODO("Not yet implemented")
    }

    override fun updateTokens(userToken: UserToken) {
        TODO("Not yet implemented")
    }

    override fun deleteTokensInList(userIdList: List<String>) {
        val userTokensInList = userTokens.filter { userIdList.contains(it.userId) }
        userTokensInList.forEach { userTokens.remove(it) }
    }

    override suspend fun getTokensFromThirdParty(thirdParty: String): List<UserToken> {
        TODO("Not yet implemented")
    }

    override fun getUserIdByExtId(extId: String, callback: (String) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getTokensFromIdList(idList: List<String>, callback: (List<UserToken>) -> Unit) {
        val userTokensInList = userTokens.filter { idList.contains(it.userId) }
        callback(userTokensInList)
    }
}