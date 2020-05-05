package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.TokenRefreshService
import dtu.openhealth.integration.shared.service.UserDataService
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import io.vertx.reactivex.core.MultiMap
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate
import io.vertx.reactivex.ext.web.codec.BodyCodec
import java.time.LocalDateTime

class OAuth2TokenRefreshServiceImpl(private val webClient: WebClient,
                                    private val parameters: OAuth2RefreshParameters,
                                    private val userDataService: UserDataService) : TokenRefreshService {

    override suspend fun refreshToken(userToken: UserToken): UserToken {
        val form = MultiMap.caseInsensitiveMultiMap()
        form.set("grant_type", "refresh_token")
        form.set("refresh_token", userToken.refreshToken)

        val promise = Promise.promise<HttpResponse<JsonObject>>()
        webClient.post(parameters.port, parameters.host, parameters.refreshPath)
                .basicAuthentication(parameters.clientId, parameters.clientSecret)
                .ssl(true)
                .expect(ResponsePredicate.SC_SUCCESS)
                .`as`(BodyCodec.jsonObject())
                .sendForm(form) { ar ->
                    if (ar.succeeded()) {
                        val response = ar.result()
                        promise.complete(response)
                    }
                    else {
                        promise.fail(ar.cause())
                    }
                }

        val response = promise.future().await()
        return saveRefreshedToken(response.body(), userToken.userId)
    }

    private suspend fun saveRefreshedToken(jsonBody: JsonObject, userId: String): UserToken {
        val accessToken = jsonBody.getString("access_token")
        val refreshToken = jsonBody.getString("refresh_token")
        val expiresIn = jsonBody.getLong("expires_in")
        val extUserId = jsonBody.getString("user_id")

        val expireDateTime = LocalDateTime.now().plusSeconds(expiresIn)

        val updatedUser = UserToken(userId, extUserId, accessToken, refreshToken, expireDateTime)
        userDataService.updateTokens(updatedUser)
        return updatedUser
    }
}