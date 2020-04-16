package dtu.openhealth.integration.shared.web

import dtu.openhealth.integration.shared.data.ThirdPartyData
import dtu.openhealth.integration.shared.model.RestEndpoint
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.http.httpServerOptionsOf
import io.vertx.kotlin.core.net.pemKeyCertOptionsOf
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.core.Vertx
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class HttpOAuth2ClientTest {

    private val responseJson = "{}"
    private val port = 8443

    @Test
    fun testOAuth2Client(vertx: Vertx, tc: VertxTestContext) {
        val endpoint = RestEndpoint(TestRestUrl("localhost", "/"), ThirdPartyData.serializer())
        val request = ApiRequest(endpoint, "/", emptyMap())
        val options = webClientOptionsOf(trustAll = true)
        val webClient = WebClient.create(vertx, options)
        val oauth2Client = HttpOAuth2ConnectorClient(webClient, port)
        initWebServer(vertx)

        val responseSingle = oauth2Client.get(request, "testToken")
        responseSingle.subscribe(
                { result -> tc.verify { assertEquals(result.responseJson, responseJson); }; tc.completeNow() },
                { error -> tc.failNow(error) }
        )
    }

    private fun initWebServer(vertx: Vertx) {
        val options = httpServerOptionsOf(
                ssl = true,
                pemKeyCertOptions = pemKeyCertOptionsOf(
                        certPath = "src/test/kotlin/dtu/openhealth/integration/shared/web/server-cert.pem",
                        keyPath = "src/test/kotlin/dtu/openhealth/integration/shared/web/server-key.pem"
                ))

        vertx.createHttpServer(options)
                .requestHandler { it.response().end(responseJson) }
                .listen(port)
    }
}
