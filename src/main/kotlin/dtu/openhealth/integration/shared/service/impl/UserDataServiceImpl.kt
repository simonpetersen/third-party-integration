package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.BaseDataService
import dtu.openhealth.integration.shared.service.UserDataService
import io.vertx.kotlin.coroutines.await
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.sqlclient.Row

class UserDataServiceImpl(vertx: Vertx) : BaseDataService(vertx), UserDataService {
    private val logger = LoggerFactory.getLogger(UserDataServiceImpl::class.java)

    override suspend fun getUserById(id: String): UserToken? {
        val sql = "SELECT * FROM USERS WHERE USERID = '$id'"
        val resultSet = executeQuery(sql).await()

        if (!resultSet.iterator().hasNext()) {
            return null
        }

        return getUserFromRow(resultSet.iterator().next())
    }

    override suspend fun getUserByExtId(extId: String): UserToken? {
        val query = "SELECT * FROM USERS WHERE EXTUSERID = '$extId'"
        val resultSet = executeQuery(query).await()

        if (!resultSet.iterator().hasNext()) {
            return null
        }

        return getUserFromRow(resultSet.iterator().next())
    }

    override fun insertUser(userToken: UserToken) {
        val query = "INSERT INTO USERTOKENS (USERID, EXTUSERID, ACCESSTOKEN, REFRESHTOKEN, EXPIREDATETIME, TOKENSECRET) " +
                "VALUES ('${userToken.userId}', '${userToken.extUserId}', '${userToken.token}', " +
                "'${userToken.refreshToken}', '${userToken.expireDateTime}', '${userToken.tokenSecret}')"

        executeQuery(query).onComplete {
            ar -> if (ar.failed()) { logger.error(ar.cause()) }
        }
    }

    override suspend fun updateTokens(userToken: UserToken) {
        val query = "UPDATE USERS SET AUTHTOKEN = '${userToken.token}', " +
                "REFRESHTOKEN = '${userToken.refreshToken}', " +
                "EXPIREDATETIME = '${userToken.expireDateTime}' " +
                "WHERE EXTUSERID = '${userToken.extUserId}' AND USERID = '${userToken.userId}'"

        executeQuery(query).await()
    }

    private fun getUserFromRow(row: Row): UserToken {
        val userId = row.getString("userid")
        val extUserId = row.getString("extuserid")
        val token = row.getString("accesstoken")
        val refreshToken = row.getString("refreshtoken")
        val expireDateTime = row.getLocalDateTime("expiredatetime")

        return UserToken(userId, extUserId, token, refreshToken, expireDateTime)
    }
}