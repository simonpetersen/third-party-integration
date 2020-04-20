package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.model.User
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

    override suspend fun refreshToken(user: User): User {
        val form = MultiMap.caseInsensitiveMultiMap()
        form.set("grant_type", "refresh_token")
        form.set("refresh_token", user.refreshToken)

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
        return saveRefreshedToken(response.body(), user.userId)
    }

    private suspend fun saveRefreshedToken(json: JsonObject, userId: String): User {
        val accessToken = json.getString("access_token")
        val refreshToken = json.getString("refresh_token")
        val expiresIn = json.getLong("expires_in")

        val expireDateTime = LocalDateTime.now().plusSeconds(expiresIn)

        val updatedUser = User(userId, token = accessToken, refreshToken = refreshToken, expireDateTime = expireDateTime)
        userDataService.updateTokens(updatedUser)
        return updatedUser
    }
}