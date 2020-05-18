package dtu.openhealth.integration.shared.web.http

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.web.ApiRequest
import dtu.openhealth.integration.shared.web.ApiResponse
import io.reactivex.Single
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate
import io.vertx.reactivex.ext.web.codec.BodyCodec


class HttpOAuth2ConnectorClient(
        private val webClient: WebClient,
        private val port: Int
): IHttpConnectorClient {

    override fun get(request: ApiRequest, userToken: UserToken): Single<ApiResponse>
    {
        return webClient.get(port, request.endpoint.url.host, request.url)
                .ssl(true)
                .bearerTokenAuthentication(userToken.token)
                .expect(ResponsePredicate.SC_SUCCESS)
                .`as`(BodyCodec.string())
                .rxSend()
                .map { ApiResponse(it.body(), request.endpoint.serializer, request.parameters) }
    }
}