package dtu.openhealth.integration.shared.service.token.revoke

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.token.revoke.data.OAuth2RevokeParameters
import dtu.openhealth.integration.shared.service.token.revoke.data.RevokeResponse
import io.reactivex.Single
import io.vertx.reactivex.core.MultiMap
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient

abstract class AOAuth2TokenRevokeService(
        private val webClient: WebClient,
        private val parameters: OAuth2RevokeParameters
): ITokenRevokeService {

    override fun revokeToken(userToken: UserToken): Single<RevokeResponse>
    {
        prepareTokenRevocation(userToken)

        val form = MultiMap.caseInsensitiveMultiMap()
        form.set("token", userToken.refreshToken)

        return webClient.post(parameters.port, parameters.host, parameters.revokeUrl)
                .ssl(parameters.ssl)
                .basicAuthentication(parameters.clientId, parameters.clientSecret)
                .rxSendForm(form)
                .map { RevokeResponse(userToken.userId, it.statusCode(), it.bodyAsString()) }
    }

    abstract fun prepareTokenRevocation(userToken: UserToken)
}