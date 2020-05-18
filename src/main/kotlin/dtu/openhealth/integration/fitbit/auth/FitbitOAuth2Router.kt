package dtu.openhealth.integration.fitbit.auth

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.web.auth.AOAuth2Router
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import io.vertx.reactivex.ext.web.client.WebClient
import java.time.LocalDateTime
import kotlin.random.Random.Default.nextInt

class FitbitOAuth2Router(
        private val vertx: Vertx,
        oauth2: OAuth2Auth,
        private val parameters: OAuth2RouterParameters,
        userTokenDataService: IUserTokenDataService
): AOAuth2Router(vertx, oauth2, parameters, userTokenDataService) {

    private val logger = LoggerFactory.getLogger(FitbitOAuth2Router::class.java)

    override fun createUser(userId: String, jsonObject: JsonObject): UserToken
    {
        val extUserId = jsonObject.getString("user_id")
        val accessToken = jsonObject.getString("access_token")
        val refreshToken = jsonObject.getString("refresh_token")
        val expiresIn = jsonObject.getLong("expires_in")
        val expireDateTime = LocalDateTime.now().plusSeconds(expiresIn)

        createSubscription(accessToken)

        return UserToken(userId, extUserId, FitbitConstants.Fitbit, accessToken, refreshToken, expireDateTime)
    }

    private fun createSubscription(accessToken: String)
    {
        val webClient = WebClient.create(vertx)
        val subscriptionId = nextInt(0,10000).toString()
        val uri = parameters.subscriptionUri.replace("[${FitbitConstants.SubscriptionId}]", subscriptionId)

        webClient.post(parameters.port, parameters.host, uri)
                .bearerTokenAuthentication(accessToken)
                .ssl(parameters.ssl)
                .send { ar ->
                    if (ar.succeeded()) {
                        val response = ar.result()
                        val logMsg = "Creating fitbit subscription. ${response.statusCode()}, ${response.bodyAsString()}"
                        logger.info(logMsg)
                    }
                    else {
                        val errorMsg = "Error when creating fitbit subscription"
                        logger.error(errorMsg)
                    }
                }
    }
}