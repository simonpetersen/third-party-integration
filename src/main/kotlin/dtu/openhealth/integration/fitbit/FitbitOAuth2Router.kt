package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.UserDataService
import dtu.openhealth.integration.shared.web.auth.OAuth2Router
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import java.time.LocalDateTime

class FitbitOAuth2Router(vertx: Vertx, oauth2: OAuth2Auth, parameters: OAuth2RouterParameters,
                         userDataService: UserDataService)
    : OAuth2Router(vertx, oauth2, parameters, userDataService) {

    override fun createUser(userId: String, jsonObject: JsonObject): User {
        val extUserId = jsonObject.getString("user_id")
        val accessToken = jsonObject.getString("access_token")
        val refreshToken = jsonObject.getString("refresh_token")
        val expiresIn = jsonObject.getLong("expires_in")
        val expireDateTime = LocalDateTime.now().plusSeconds(expiresIn)

        return User(userId, extUserId, accessToken, refreshToken, expireDateTime)
    }
}