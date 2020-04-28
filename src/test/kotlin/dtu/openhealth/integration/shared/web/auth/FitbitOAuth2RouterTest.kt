package dtu.openhealth.integration.shared.web.auth

import com.nhaarman.mockitokotlin2.mock
import dtu.openhealth.integration.fitbit.FitbitOAuth2Router
import dtu.openhealth.integration.shared.service.UserDataService
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.junit5.Checkpoint
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.ext.auth.oauth2.oAuth2ClientOptionsOf
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.ext.web.handler.BodyHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class FitbitOAuth2RouterTest {

    private val userId = "test_user_2"
    private val redirectUri = "http://localhost:8080/login"
    private val redirectBody = "Redirect to localhost/oauth2"
    private val authCode = "abcd12345"
    private val accessToken = "access_token_$authCode"
    private val refreshToken = "refresh_token_$authCode"
    private val oauth2Options = oAuth2ClientOptionsOf(
            authorizationPath = "http://localhost:8080/oauth2",
            flow = OAuth2FlowType.AUTH_CODE,
            clientID = "ID_123",
            clientSecret = "SECRET_456",
            tokenPath = "http://localhost:8080/oauth2/token")

    @Test
    fun testOAuth2RouterRedirect(vertx: Vertx, tc: VertxTestContext) {
        prepareWebServerAndRunTest(vertx, tc) { vx, vxTc -> oauth2RouterRedirect(vx, vxTc)}
    }

    private fun oauth2RouterRedirect(vertx: Vertx, tc: VertxTestContext) {
        val client = WebClient.create(vertx)
        client.get(8080, "localhost", "/auth/$userId")
                .send { ar ->
                    if (ar.succeeded()) {
                        val response = ar.result()
                        assertThat(response.statusCode()).isEqualTo(200)
                        assertThat(response.body().toString()).isEqualTo(redirectBody)
                        tc.completeNow()
                    }
                    else {
                        tc.failNow(ar.cause())
                    }
                }
    }

    @Test
    fun testOAuth2RouterCallback(vertx: Vertx, tc: VertxTestContext) {
        prepareWebServerAndRunTest(vertx, tc) {
            vx, vxTc -> oauth2RouterCallback(vx, vxTc)
            vxTc.completeNow()
        }
    }

    private fun oauth2RouterCallback(vertx: Vertx, tc: VertxTestContext) {
        val checkpoint = tc.checkpoint()
        val client = WebClient.create(vertx)
        client.get(8080, "localhost", "/login?code=$authCode")
                .send { ar ->
                    if (ar.succeeded()) {
                        tc.verify {
                            val response = ar.result()
                            assertThat(response.statusCode()).isEqualTo(200)
                            assertThat(response.body().toString()).isEqualTo("User authenticated. Back to you CARP.")
                        }
                        checkpoint.flag()
                    }
                    else {
                        tc.failNow(ar.cause())
                    }
                }
    }

    private fun prepareWebServerAndRunTest(vertx: Vertx, tc: VertxTestContext,
                                           testFunction: (Vertx, VertxTestContext) -> Unit) {
        val tokenPostCheckpoint = tc.checkpoint()
        val oauth2 = OAuth2Auth.create(vertx, oauth2Options)
        val parameters = OAuth2RouterParameters(redirectUri, "", "activity")
        val userDataService : UserDataService = mock()
        val authenticationRouter = FitbitOAuth2Router(vertx,oauth2,parameters,userDataService).getRouter()

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.get("/oauth2").handler { authorizeHandler(it) }
        router.post("/oauth2/token").handler { oauth2Token(it, tokenPostCheckpoint) }
        router.mountSubRouter("/", authenticationRouter)
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, tc.succeeding {
                    testFunction(vertx, tc)
                })
    }

    private fun authorizeHandler(routingContext: RoutingContext) {
        routingContext.response().end(redirectBody)
    }

    private fun oauth2Token(routingContext: RoutingContext, checkpoint: Checkpoint) {
        val code = routingContext.request().getParam("code")
        val uri = routingContext.request().getParam("redirect_uri")
        assertThat(code).isEqualTo(authCode)
        assertThat(uri).isEqualTo(redirectUri)
        checkpoint.flag()

        // Return token info
        val tokenInfo = json {
            obj(
                    "access_token" to accessToken,
                    "expires_in" to "3600",
                    "refresh_token" to refreshToken,
                    "user_id" to userId
            )
        }
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(tokenInfo.toString())
    }
}
