package dtu.openhealth.integration.shared.web

import dtu.openhealth.integration.shared.model.ThirdPartyData
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.http.httpServerOptionsOf
import io.vertx.kotlin.core.net.pemKeyCertOptionsOf
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.http.HttpServerRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class HttpOAuth2ClientTest {

    private val responseJson = "{}"
    private val port = 8443
    private val testToken = UserToken("id123", "extUserId123","hjkhfakelfbqjkwbf")

    @Test
    fun testOAuth2Client(vertx: Vertx, tc: VertxTestContext) {
        val endpoint = RestEndpoint(TestRestUrl("localhost", "/"), ThirdPartyData.serializer())
        val request = ApiRequest(endpoint, "/", emptyMap())
        val options = httpServerOptionsOf(
                ssl = true,
                pemKeyCertOptions = pemKeyCertOptionsOf(
                        certPath = "src/test/kotlin/dtu/openhealth/integration/shared/web/server-cert.pem",
                        keyPath = "src/test/kotlin/dtu/openhealth/integration/shared/web/server-key.pem"
                ))

        vertx.createHttpServer(options)
                .requestHandler { handleRequest(it) }
                .listen(port, tc.succeeding {
                    val clientOptions = webClientOptionsOf(trustAll = true)
                    val webClient = WebClient.create(vertx, clientOptions)
                    val oauth2Client = HttpOAuth2ConnectorClient(webClient, port)

                    val responseSingle = oauth2Client.get(request, testToken)
                    responseSingle.subscribe(
                            { result ->
                                tc.verify { assertThat(result.responseJson).isEqualTo(responseJson) }
                                tc.completeNow() },
                            { error -> tc.failNow(error) }
                    )
                })
    }

    private fun handleRequest(serverRequest: HttpServerRequest) {
        val authHeader = serverRequest.getHeader("Authorization")
        val expectedAuthHeader = "Bearer ${testToken.token}"
        assertThat(authHeader).isEqualTo(expectedAuthHeader)
        serverRequest.response().end(responseJson)
    }
}
