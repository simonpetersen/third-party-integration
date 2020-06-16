package dtu.openhealth.integration.shared.web.http

import dtu.openhealth.integration.shared.model.AThirdPartyData
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.mock.LocalhostRestUrl
import dtu.openhealth.integration.shared.web.ApiRequest
import io.vertx.junit5.Checkpoint
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

    private val port = 8443
    private val thirdParty = "thirdParty"
    private val userId = "id123"
    private val testToken = UserToken(userId, "extUserId123", thirdParty,"hjkhfakelfbqjkwbf")
    private val responseJson = "{ \"user\": \"$userId\"}"

    @Test
    fun testOAuth2Client(vertx: Vertx, tc: VertxTestContext)
    {
        val checkpoint = tc.checkpoint()
        val options = httpServerOptionsOf(
                ssl = true,
                pemKeyCertOptions = pemKeyCertOptionsOf(
                        certPath = "test-server-cert.pem",
                        keyPath = "test-server-key.pem"
                ))

        vertx.createHttpServer(options)
                .requestHandler { handleRequest(it, checkpoint) }
                .listen(port, tc.succeeding {
                    testFunction(vertx, tc)
                })
    }

    private fun testFunction(vertx: Vertx, tc: VertxTestContext)
    {
        val checkpoint = tc.checkpoint()
        val endpoint = RestEndpoint(LocalhostRestUrl( "/"), AThirdPartyData.serializer())
        val request = ApiRequest(endpoint, "/", emptyMap())
        val clientOptions = webClientOptionsOf(trustAll = true)
        val webClient = WebClient.create(vertx, clientOptions)
        val oauth2Client = HttpOAuth2ConnectorClient(webClient, port)

        val responseSingle = oauth2Client.get(request, testToken)
        responseSingle.subscribe(
                { result ->
                    tc.verify { assertThat(result.responseJson).isEqualTo(responseJson) }
                    checkpoint.flag()
                },
                { error -> tc.failNow(error) }
        )
    }

    private fun handleRequest(serverRequest: HttpServerRequest, checkpoint: Checkpoint)
    {
        val authHeader = serverRequest.getHeader("Authorization")
        val expectedAuthHeader = "Bearer ${testToken.token}"
        assertThat(authHeader).isEqualTo(expectedAuthHeader)
        checkpoint.flag()
        serverRequest.response().end(responseJson)
    }
}
