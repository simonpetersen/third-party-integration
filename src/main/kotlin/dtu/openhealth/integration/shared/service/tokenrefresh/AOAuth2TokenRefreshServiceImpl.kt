package dtu.openhealth.integration.shared.service.tokenrefresh

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import io.vertx.reactivex.core.MultiMap
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate
import io.vertx.reactivex.ext.web.codec.BodyCodec

abstract class AOAuth2TokenRefreshServiceImpl(
        private val webClient: WebClient,
        private val parameters: OAuth2RefreshParameters,
        private val userTokenDataService: IUserTokenDataService
): ITokenRefreshService {

    override suspend fun refreshToken(userToken: UserToken): UserToken
    {
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

    abstract fun updatedUserToken(jsonBody: JsonObject, userId: String): UserToken

    private suspend fun saveRefreshedToken(jsonBody: JsonObject, userId: String): UserToken
    {
        val updatedUser = updatedUserToken(jsonBody, userId)
        userTokenDataService.updateTokens(updatedUser)

        return updatedUser
    }
}