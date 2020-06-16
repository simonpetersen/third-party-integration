package dtu.openhealth.integration.fitbit.service.token.revoke

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.token.refresh.ITokenRefreshService
import dtu.openhealth.integration.shared.service.token.revoke.AOAuth2TokenRevokeService
import dtu.openhealth.integration.shared.service.token.revoke.data.OAuth2RevokeParameters
import io.vertx.core.AsyncResult
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import java.time.LocalDateTime

class FitbitTokenRevokeService(
        private val webClient: WebClient,
        private val parameters: OAuth2RevokeParameters,
        private val tokenRefreshService: ITokenRefreshService
): AOAuth2TokenRevokeService(webClient, parameters) {

    private val logger = LoggerFactory.getLogger(FitbitTokenRevokeService::class.java)

    override fun prepareTokenRevocation(userToken: UserToken)
    {
        if (tokenIsExpired(userToken.expireDateTime)) {
            tokenRefreshService.refreshToken(userToken) {
                updatedToken -> deleteSubscription(updatedToken)
            }
        }

        deleteSubscription(userToken)
    }

    private fun deleteSubscription(userToken: UserToken)
    {
        val uri = parameters.subscriptionUrl.replace("[${FitbitConstants.SubscriptionId}]", userToken.userId)
        webClient.delete(parameters.port, parameters.host, uri)
                .ssl(parameters.ssl)
                .bearerTokenAuthentication(userToken.token)
                .send { handleAsyncResult(it, userToken) }
    }

    private fun handleAsyncResult(ar: AsyncResult<HttpResponse<Buffer>>, userToken: UserToken)
    {
        if (ar.succeeded()) {
            if (ar.result().statusCode() == 204) {
                val infoMsg = "Fitbit subscription successfully deleted for $userToken"
                logger.info(infoMsg)
            }
            else {
                val errorMsg = "Error when deleting subscription for $userToken. Status = ${ar.result().statusCode()}. Response = ${ar.result().bodyAsString()}"
                logger.error(errorMsg)
            }
        }
        else {
            val errorMsg = "Error when deleting subscription for $userToken"
            logger.error(errorMsg, ar.cause())
        }
    }

    private fun tokenIsExpired(expireDateTime: LocalDateTime?): Boolean
    {
        if (expireDateTime == null) {
            return false
        }

        val now = LocalDateTime.now()
        return expireDateTime.isBefore(now)
    }
}