package dtu.openhealth.integration.shared.service.token.refresh

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.coroutines.await
import io.vertx.reactivex.core.MultiMap
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.ext.web.codec.BodyCodec

abstract class AOAuth2TokenRefreshService(
        private val webClient: WebClient,
        private val parameters: OAuth2RefreshParameters,
        private val userTokenDataService: IUserTokenDataService
): ITokenRefreshService {

    private val logger = LoggerFactory.getLogger(AOAuth2TokenRefreshService::class.java)

    override fun refreshToken(userToken: UserToken, callback: (UserToken) -> Unit)
    {
        val form = MultiMap.caseInsensitiveMultiMap()
        form.set("grant_type", "refresh_token")
        form.set("refresh_token", userToken.refreshToken)

        webClient.post(parameters.port, parameters.host, parameters.refreshPath)
                .basicAuthentication(parameters.clientId, parameters.clientSecret)
                .ssl(true)
                .`as`(BodyCodec.jsonObject())
                .sendForm(form) { ar ->
                    if (ar.succeeded()) {
                        val response = ar.result()
                        handleHttpResponse(userToken, response, callback)
                    }
                    else {
                        val errorMsg = "Error refreshing token for $userToken"
                        logger.error(errorMsg, ar.cause())
                    }
                }
    }

    abstract fun updatedUserToken(jsonBody: JsonObject, userId: String): UserToken

    private fun handleHttpResponse(userToken: UserToken, response: HttpResponse<JsonObject>, callback: (UserToken) -> Unit)
    {
        if (response.statusCode() != 200)
        {
            val errorMsg = "Token refresh failed for $userToken. Status code = ${response.statusCode()}. Response = ${response.bodyAsString()}"
            logger.error(errorMsg)
            return
        }

        val updatedUser = saveRefreshedToken(response.body(), userToken.userId)
        callback(updatedUser)
    }

    private fun saveRefreshedToken(jsonBody: JsonObject, userId: String): UserToken
    {
        val updatedUser = updatedUserToken(jsonBody, userId)
        userTokenDataService.updateTokens(updatedUser)

        return updatedUser
    }
}