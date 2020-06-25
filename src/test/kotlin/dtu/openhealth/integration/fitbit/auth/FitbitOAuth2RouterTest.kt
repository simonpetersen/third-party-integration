package dtu.openhealth.integration.fitbit.auth

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class FitbitOAuth2RouterTest {

    private val userId = "test_user_2"
    private val oauthPort = 8181
    private val testPort = oauthPort + 1
    private val redirectUri = "http://localhost:$oauthPort/login"
    private val returnUri = "http://localhost:$oauthPort/result"
    private val subscriptionPath = "/apiSubscriptions/[subscription-id]"
    private val redirectBody = "Redirect to localhost/oauth2"
    private val authCode = "abcd12345"
    private val accessToken = "access_token_$authCode"
    private val refreshToken = "refresh_token_$authCode"
    private val oauth2Options = oAuth2ClientOptionsOf(
            authorizationPath = "http://localhost:$testPort/oauth2",
            flow = OAuth2FlowType.AUTH_CODE,
            clientID = "ID_123",
            clientSecret = "SECRET_456",
            tokenPath = "http://localhost:$testPort/oauth2/token")

    @Test
    fun testOAuth2RouterRedirect(vertx: Vertx, tc: VertxTestContext)
    {
        val userDataService : IUserTokenDataService = mock()
        prepareWebServerAndRunTest(vertx, tc, userDataService) { vx, vxTc -> oauth2RouterRedirect(vx, vxTc)}
    }

    private fun oauth2RouterRedirect(vertx: Vertx, tc: VertxTestContext)
    {
        val client = WebClient.create(vertx)
        client.get(oauthPort, "localhost", "/auth/$userId")
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
    fun testOAuth2RouterCallback(vertx: Vertx, tc: VertxTestContext)
    {
        val userDataService : IUserTokenDataService = mock()
        prepareWebServerAndRunTest(vertx, tc, userDataService) {
            vx, vxTc -> oauth2RouterCallback(vx, vxTc, userDataService)
        }
    }

    private fun oauth2RouterCallback(vertx: Vertx, tc: VertxTestContext, userTokenDataService: IUserTokenDataService)
    {
        val checkpoint = tc.checkpoint()
        val client = WebClient.create(vertx)
        client.get(oauthPort, "localhost", "/callback?code=$authCode&state=$userId")
                .send { ar ->
                    if (ar.succeeded()) {
                        tc.verify {
                            val response = ar.result()
                            assertThat(response.statusCode()).isEqualTo(200)
                            assertThat(response.body().toString()).isEqualTo("User authenticated.")
                            verify(userTokenDataService).insertUserToken(any())
                        }
                        checkpoint.flag()
                    }
                    else {
                        tc.failNow(ar.cause())
                    }
                }
    }

    private fun prepareWebServerAndRunTest(vertx: Vertx,
                                           tc: VertxTestContext,
                                           userTokenDataService : IUserTokenDataService,
                                           testFunction: (Vertx, VertxTestContext) -> Unit)
    {
        val tokenPostCp = tc.checkpoint()
        val subscriptionCreationCp = tc.checkpoint()
        val oauth2 = OAuth2Auth.create(vertx, oauth2Options)
        val parameters = OAuth2RouterParameters(
                redirectUri,
                returnUri,
                "activity",
                "localhost",
                subscriptionPath,
                testPort,
                ssl = false
        )
        val authRouter = FitbitOAuth2Router(vertx, oauth2, parameters, userTokenDataService).getRouter()
        vertx.createHttpServer().requestHandler(authRouter).listen(oauthPort)

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.get("/oauth2").handler { authoriseHandler(it) }
        router.post("/oauth2/token").handler { oauth2Token(it, tokenPostCp) }
        router.post("/apiSubscriptions/:subscriptionId").handler { subscriptionCreation(it, subscriptionCreationCp) }
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(testPort, tc.succeeding {
                    testFunction(vertx, tc)
                })
    }

    private fun authoriseHandler(routingContext: RoutingContext)
    {
        routingContext.response().end(redirectBody)
    }

    private fun oauth2Token(routingContext: RoutingContext, checkpoint: Checkpoint)
    {
        val code = routingContext.request().getParam("code")
        val uri = routingContext.request().getParam("redirect_uri")
        assertThat(code).isEqualTo(authCode)
        assertThat(uri).isEqualTo(redirectUri)
        checkpoint.flag()

        // Return token info
        val tokenInfo = json {
            obj(
                    "access_token" to accessToken,
                    "expires_in" to 3600,
                    "refresh_token" to refreshToken,
                    "user_id" to userId
            )
        }
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(tokenInfo.toString())
    }

    private fun subscriptionCreation(routingContext: RoutingContext, checkpoint: Checkpoint)
    {
        val subscriptionId = routingContext.request().getParam("subscriptionId")
        assertNotNull(subscriptionId)
        assertThat(subscriptionId).isEqualTo(userId)
        checkpoint.flag()
        routingContext.response().setStatusCode(201).end()
    }
}