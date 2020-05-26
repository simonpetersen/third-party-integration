package dtu.openhealth.integration.fitbit.service.token.refresh

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.token.refresh.AOAuth2TokenRefreshService
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.ext.web.client.WebClient
import java.time.LocalDateTime

class FitbitTokenRefreshServiceImpl(
        webClient: WebClient,
        parameters: OAuth2RefreshParameters,
        userTokenDataService: IUserTokenDataService
): AOAuth2TokenRefreshService(webClient, parameters, userTokenDataService) {

    override fun updatedUserToken(jsonBody: JsonObject, userId: String): UserToken {
        val accessToken = jsonBody.getString("access_token")
        val refreshToken = jsonBody.getString("refresh_token")
        val expiresIn = jsonBody.getLong("expires_in")
        val extUserId = jsonBody.getString("user_id")

        val expireDateTime = LocalDateTime.now().plusSeconds(expiresIn)

        return UserToken(userId, extUserId, FitbitConstants.Fitbit, accessToken, refreshToken, expireDateTime)
    }

}