package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.BaseDataService
import dtu.openhealth.integration.shared.service.UserDataService
import io.vertx.kotlin.coroutines.await
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.sqlclient.Row

class VertxUserServiceImpl(vertx: Vertx) : BaseDataService(vertx), UserDataService {
    private val logger = LoggerFactory.getLogger(VertxUserServiceImpl::class.java)

    override suspend fun getUserById(id: String): User? {
        val sql = "SELECT * FROM USERS WHERE USERID = '$id'"
        val resultSet = executeQuery(sql).await()

        if (!resultSet.iterator().hasNext()) {
            return null
        }

        return getUserFromRow(resultSet.iterator().next())
    }

    override suspend fun getUserByExtId(extId: String): User? {
        val query = "SELECT * FROM USERS WHERE EXTUSERID = '$extId'"
        val resultSet = executeQuery(query).await()

        if (!resultSet.iterator().hasNext()) {
            return null
        }

        return getUserFromRow(resultSet.iterator().next())
    }

    override fun createUser(user: User) {
        val query = "INSERT INTO USERS (USERID, EXTUSERID, AUTHTOKEN, REFRESHTOKEN, TOKENEXPIRETIME) " +
                "VALUES ('${user.userId}', '${user.extUserId}', '${user.token}', '${user.refreshToken}', '${user.expireDateTime}')"

        executeQuery(query).onComplete {
            ar -> if (ar.failed()) { logger.error(ar.cause()) }
        }
    }

    override suspend fun updateTokens(user: User) {
        val query = "UPDATE USERS SET AUTHTOKEN = '${user.token}', " +
                "REFRESHTOKEN = '${user.refreshToken}', " +
                "TOKENEXPIRETIME = '${user.expireDateTime}' " +
                "WHERE EXTUSERID = '${user.extUserId}' AND USERID = '${user.userId}'"

        executeQuery(query).await()
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