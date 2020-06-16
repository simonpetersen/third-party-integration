package dtu.openhealth.integration.shared.service.data.usertoken

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.BaseDataService
import io.vertx.kotlin.coroutines.await
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet

class UserTokenDataServiceImpl(
        vertx: Vertx
): BaseDataService(vertx), IUserTokenDataService {

    private val logger = LoggerFactory.getLogger(UserTokenDataServiceImpl::class.java)

    override suspend fun getUserById(id: String): UserToken?
    {
        val sql = "SELECT * FROM USERTOKENS WHERE USERID = '$id'"
        val resultSet = executeQuery(sql).await()

        if (!resultSet.iterator().hasNext()) {
            return null
        }

        return getUserTokenFromRow(resultSet.iterator().next())
    }

    override suspend fun getUserByExtId(extId: String): UserToken?
    {
        val query = "SELECT * FROM USERTOKENS WHERE EXTUSERID = '$extId'"
        val resultSet = executeQuery(query).await()

        if (!resultSet.iterator().hasNext()) {
            return null
        }

        return getUserTokenFromRow(resultSet.iterator().next())
    }

    override fun insertUserToken(userToken: UserToken)
    {
        val expireDataTime = if (userToken.expireDateTime != null) "'${userToken.expireDateTime}'" else "null"
        val query = "INSERT INTO USERTOKENS (USERID, EXTUSERID, THIRDPARTY, ACCESSTOKEN, REFRESHTOKEN, EXPIREDATETIME, TOKENSECRET) " +
                "VALUES ('${userToken.userId}', '${userToken.extUserId}', '${userToken.thirdParty}', " +
                "'${userToken.token}', '${userToken.refreshToken}', $expireDataTime, '${userToken.tokenSecret}')"

        executeQuery(query).onComplete { ar ->
            if (ar.failed()) {
                val errorMsg = "Error inserting $userToken in database"
                logger.error(errorMsg, ar.cause())
            }
        }
    }

    override fun updateTokens(userToken: UserToken)
    {
        val query = "UPDATE USERTOKENS SET ACCESSTOKEN = '${userToken.token}', " +
                "REFRESHTOKEN = '${userToken.refreshToken}', " +
                "EXPIREDATETIME = '${userToken.expireDateTime}' " +
                "WHERE EXTUSERID = '${userToken.extUserId}' AND USERID = '${userToken.userId}'"

        executeQuery(query).onComplete { ar ->
            if (ar.failed()) {
                val errorMsg = "Error updating $userToken in database"
                logger.error(errorMsg, ar.cause())
            }
        }
    }

    override fun deleteTokensInList(userIdList: List<String>) {
        val userList = userIdList.joinToString { "'$it'" }
        val query = "DELETE FROM USERTOKENS WHERE USERID IN ($userList)"

        executeQuery(query).onComplete { ar ->
            if (ar.failed()) {
                val errorMsg = "Error when deleting user list"
                logger.error(errorMsg, ar.cause())
            }
        }
    }

    override suspend fun getTokensFromThirdParty(thirdParty: String): List<UserToken> {
        val query = "SELECT * FROM USERTOKENS WHERE thirdparty = '$thirdParty'"
        val resultSet = executeQuery(query).await()

        return getUserTokensFromResultSet(resultSet)
    }

    override fun getUserIdByExtId(extId: String, callback: (String) -> Unit)
    {
        val query = "SELECT USERID FROM USERTOKENS WHERE EXTUSERID = '$extId'"
        executeQuery(query).onComplete {ar ->
            if (ar.succeeded()) {
                getUserIdAndCallback(ar.result(), extId, callback)
            }
            else {
                val errorMsg = "Error getting userId from $extId from database"
                logger.error(errorMsg, ar.cause())
            }
        }
    }

    override fun getTokensFromIdList(idList: List<String>, callback: (List<UserToken>) -> Unit) {
        val userList = idList.joinToString { "'$it'" }
        val query = "SELECT * FROM USERTOKENS WHERE USERID IN ($userList)"

        executeQuery(query).onComplete {ar ->
            if (ar.succeeded()) {
                val userTokenList = getUserTokensFromResultSet(ar.result())
                callback(userTokenList)
            }
            else {
                val errorMsg = "Error when reading user list from ids $idList"
                logger.error(errorMsg, ar.cause())
            }
        }
    }

    private fun getUserIdAndCallback(result: RowSet<Row>, extId: String, callback: (String) -> Unit)
    {
        if (!result.iterator().hasNext()) {
            logger.error("No UserToken found for external id $extId")
        }
        else {
            val row = result.iterator().next()
            val userId = row.getString("userid")
            callback(userId)
        }
    }

    private fun getUserTokensFromResultSet(resultSet: RowSet<Row>): List<UserToken>
    {
        val iterator = resultSet.iterator()
        val tokenList = mutableListOf<UserToken>()
        while (iterator.hasNext()) {
            tokenList.add(getUserTokenFromRow(iterator.next()))
        }

        return tokenList
    }

    private fun getUserTokenFromRow(row: Row): UserToken
    {
        val userId = row.getString("userid")
        val extUserId = row.getString("extuserid")
        val thirdParty = row.getString("thirdparty")
        val token = row.getString("accesstoken")
        val refreshToken = row.getString("refreshtoken")
        val expireDateTime = row.getLocalDateTime("expiredatetime")
        val tokenSecret = row.getString("tokensecret")

        return UserToken(userId, extUserId, thirdParty, token, refreshToken, expireDateTime, tokenSecret)
    }
}
