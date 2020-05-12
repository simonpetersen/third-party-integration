package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.BaseDataService
import dtu.openhealth.integration.shared.service.UserDataService
import io.vertx.kotlin.coroutines.await
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet

class UserDataServiceImpl(vertx: Vertx) : BaseDataService(vertx), UserDataService {
    private val logger = LoggerFactory.getLogger(UserDataServiceImpl::class.java)

    override suspend fun getUserById(id: String): UserToken? {
        val sql = "SELECT * FROM USERTOKENS WHERE USERID = '$id'"
        val resultSet = executeQuery(sql).await()

        if (!resultSet.iterator().hasNext()) {
            return null
        }

        return getUserFromRow(resultSet.iterator().next())
    }

    override suspend fun getUserByExtId(extId: String): UserToken? {
        val query = "SELECT * FROM USERTOKENS WHERE EXTUSERID = '$extId'"
        val resultSet = executeQuery(query).await()

        if (!resultSet.iterator().hasNext()) {
            return null
        }

        return getUserFromRow(resultSet.iterator().next())
    }

    override fun insertUser(userToken: UserToken) {
        val expireDataTime = if (userToken.expireDateTime != null) "'${userToken.expireDateTime}'" else "null"
        val query = "INSERT INTO USERTOKENS (USERID, EXTUSERID, ACCESSTOKEN, REFRESHTOKEN, EXPIREDATETIME, TOKENSECRET) " +
                "VALUES ('${userToken.userId}', '${userToken.extUserId}', '${userToken.token}', " +
                "'${userToken.refreshToken}', $expireDataTime, '${userToken.tokenSecret}')"

        executeQuery(query).onComplete {
            ar -> if (ar.failed()) { logger.error(ar.cause()) }
        }
    }

    override suspend fun updateTokens(userToken: UserToken) {
        val query = "UPDATE USERTOKENS SET ACCESSTOKEN = '${userToken.token}', " +
                "REFRESHTOKEN = '${userToken.refreshToken}', " +
                "EXPIREDATETIME = '${userToken.expireDateTime}' " +
                "WHERE EXTUSERID = '${userToken.extUserId}' AND USERID = '${userToken.userId}'"

        executeQuery(query).onComplete { ar ->
            if (ar.failed()) {
                logger.error(ar.cause())
            }
        }
    }

    override fun getUserIdByExtId(extId: String, callback: (String) -> Unit) {
        val query = "SELECT USERID FROM USERTOKENS WHERE EXTUSERID = '$extId'"
        executeQuery(query).onComplete {ar ->
            if (ar.succeeded()) {
                getUserIdAndCallback(ar.result(), callback)
            }
            else {
                logger.error(ar.cause())
            }
        }
    }

    private fun getUserIdAndCallback(result: RowSet<Row>, callback: (String) -> Unit) {
        if (!result.iterator().hasNext()) {
            logger.error("No UserToken found")
        }
        else {
            val row = result.iterator().next()
            val userId = row.getString("userid")
            callback(userId)
        }
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