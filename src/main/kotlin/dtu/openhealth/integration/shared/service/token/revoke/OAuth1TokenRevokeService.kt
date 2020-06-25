package dtu.openhealth.integration.shared.service.token.revoke

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import dtu.openhealth.integration.garmin.data.GarminConstants
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.token.revoke.data.OAuth1RevokeParameters
import dtu.openhealth.integration.shared.service.token.revoke.data.RevokeResponse
import io.reactivex.Single
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.ext.web.client.WebClient


class OAuth1TokenRevokeService(
        private val webClient: WebClient,
        private val parameters: OAuth1RevokeParameters
): ITokenRevokeService {

    private val oauthService = buildOAuthService()
    private val logger = LoggerFactory.getLogger(OAuth1TokenRevokeService::class.java)

    override fun revokeToken(userToken: UserToken): Single<RevokeResponse>
    {
        logger.info("Revoking token for user: ${userToken.userId}")
        val authHeader = generateAuthHeader(userToken)
        return webClient.delete(parameters.port, parameters.host, parameters.revokeUrl)
                .ssl(parameters.ssl)
                .putHeader(GarminConstants.Auth, authHeader)
                .rxSend()
                .map {
                    logger.info("Revoke token response for user ${userToken.userId}: " +
                            "body: ${it.bodyAsString()} " +
                            "statusCode: ${it.statusCode()}. ")
                    RevokeResponse(userToken.userId, it.statusCode(), it.bodyAsString())
                }
    }

    private fun generateAuthHeader(userToken: UserToken): String?
    {
        val fullRevokeUrl = "https://${parameters.host}${parameters.revokeUrl}"
        val accessToken = OAuth1AccessToken(userToken.token, userToken.tokenSecret)
        val request = OAuthRequest(Verb.DELETE, fullRevokeUrl)
        oauthService.signRequest(accessToken, request)

        return request.headers[GarminConstants.Auth]
    }

    private fun buildOAuthService() : OAuth10aService
    {
        return ServiceBuilder(parameters.consumerKey)
                .apiSecret(parameters.consumerSecret)
                .build(parameters.api)
    }
}
