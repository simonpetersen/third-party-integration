package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.BaseDataService
import dtu.openhealth.integration.shared.service.UserDataService
import io.vertx.kotlin.coroutines.await
import io.vertx.core.Vertx
import io.vertx.sqlclient.Row

class VertxUserServiceImpl(vertx: Vertx) : BaseDataService(vertx), UserDataService {
    override suspend fun getUserById(id: String): User? {
        val sql = "SELECT * FROM USERS WHERE USERID = '$id'"
        val resultSet = executeQuery(sql).await()

        if (!resultSet.iterator().hasNext()) {
            return null
        }

        return getUserFromRow(resultSet.iterator().next())
    }

    override suspend fun getUserByExtId(extId: String): User? {
        val sql = "SELECT * FROM USERS WHERE EXTUSERID = '$extId'"
        val resultSet = executeQuery(sql).await()

        if (!resultSet.iterator().hasNext()) {
            return null
        }

        return getUserFromRow(resultSet.iterator().next())
    }

    override suspend fun createUser(user: User) {
        val sql = "INSERT INTO USERS (USERID, EXTUSERID, AUTHTOKEN, REFRESHTOKEN, TOKENEXPIRETIME) " +
                "VALUES ('${user.userId}', '${user.extUserId}', '${user.token}', '${user.refreshToken}', '${user.expireDateTime}')"

        executeQuery(sql).await()
    }

    override suspend fun updateTokens(user: User) {
        TODO("Not yet implemented")
    }

    private fun getUserFromRow(row: Row): User {
        val userId = row.getString("userid")
        val extUserId = row.getString("extuserid")
        val token = row.getString("authtoken")
        val refreshToken = row.getString("refreshtoken")
        val expireDateTime = row.getLocalDateTime("tokenexpiretime")

        return User(userId, extUserId, token, refreshToken, expireDateTime)
    }
}