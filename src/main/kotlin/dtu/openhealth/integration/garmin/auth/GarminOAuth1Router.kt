package dtu.openhealth.integration.garmin.auth

import dtu.openhealth.integration.garmin.data.GarminConstants
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.web.auth.AOAuth1Router
import dtu.openhealth.integration.shared.web.parameters.OAuth1RouterParameters
import io.vertx.reactivex.core.Vertx

class GarminOAuth1Router(
        vertx: Vertx,
        parameters: OAuth1RouterParameters,
        userTokenDataService: IUserTokenDataService,
        requestTokenSecrets : MutableMap<String, String> = mutableMapOf()
): AOAuth1Router(vertx, parameters, userTokenDataService, requestTokenSecrets) {

    override fun getUserToken(userId: String, accessToken: String, tokenSecret: String): UserToken
    {
        return UserToken(userId, accessToken, GarminConstants.Garmin, accessToken, tokenSecret = tokenSecret)
    }
}