package dtu.openhealth.integration.fitbit.auth

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.web.auth.AOAuth2Router
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import java.time.LocalDateTime

class FitbitOAuth2Router(
        vertx: Vertx,
        oauth2: OAuth2Auth,
        parameters: OAuth2RouterParameters,
        userTokenDataService: IUserTokenDataService
): AOAuth2Router(vertx, oauth2, parameters, userTokenDataService) {

    override fun createUser(userId: String, jsonObject: JsonObject): UserToken
    {
        val extUserId = jsonObject.getString("user_id")
        val accessToken = jsonObject.getString("access_token")
        val refreshToken = jsonObject.getString("refresh_token")
        val expiresIn = jsonObject.getLong("expires_in")
        val expireDateTime = LocalDateTime.now().plusSeconds(expiresIn)

        return UserToken(userId, extUserId, FitbitConstants.Fitbit, accessToken, refreshToken, expireDateTime)
    }
}