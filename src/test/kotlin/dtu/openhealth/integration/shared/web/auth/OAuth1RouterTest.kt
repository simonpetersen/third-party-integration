package dtu.openhealth.integration.shared.web.auth

import com.nhaarman.mockitokotlin2.mock
import dtu.openhealth.integration.shared.service.UserDataService
import dtu.openhealth.integration.shared.web.parameters.OAuth1RouterParameters
import io.vertx.junit5.Checkpoint
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.ext.web.handler.BodyHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class OAuth1RouterTest {
    private val userId = "test_user_2"
    private val consumerKey = "22d54322-2d2d-g67j-9876-234rgf264567"
    private val redirectUri = "http://localhost:8083/callback"
    private val redirectBody = "Redirect to localhost/oauthConfirm"
    private val authCode = "6789efgh"
    private val oauthVerifier = "verifier_$authCode"
    private val requestToken = "request_token_$authCode"
    private val requestTokenSecret = "request_token_secret_$authCode"
    private val accessToken = "access_token_$authCode"
    private val accessTokenSecret = "access_token_secret_$authCode"

    @Test
    fun testOAuth1RouterRedirect(vertx: Vertx, tc: VertxTestContext) {
        val checkpoint = tc.checkpoint()
        val requestTokenCheckpoint = tc.checkpoint()
        val oauthConfirmToken = tc.checkpoint()
        initWebServer(vertx, tc, requestTokenCheckpoint, oauthConfirmToken,null)

        val client = WebClient.create(vertx)
        client.get(8083, "localhost", "/auth/$userId")
                .send { ar ->
                    if (ar.succeeded()) {
                        tc.verify {
                            val response = ar.result()
                            assertThat(response.statusCode()).isEqualTo(200)
                            assertThat(response.body().toString()).isEqualTo(redirectBody)
                        }
                        checkpoint.flag()
                    }
                    else {
                        tc.failNow(ar.cause())
                    }
                }
    }

    @Test
    fun testOAuth1AccessToken(vertx: Vertx, tc: VertxTestContext) {
        val accessTokenCheckpoint = tc.checkpoint()
        initWebServer(vertx, tc, null,null, accessTokenCheckpoint)

        val requestUri = "/callback/$userId?oauth_token=$requestToken&oauth_verifier=$oauthVerifier"
        val checkpoint = tc.checkpoint()
        val client = WebClient.create(vertx)
        client.get(8083, "localhost", requestUri)
                .send { ar ->
                    if (ar.succeeded()) {
                        tc.verify {
                            val response = ar.result()
                            assertThat(response.statusCode()).isEqualTo(200)
                        }
                        checkpoint.flag()
                    }
                    else {
                        tc.failNow(ar.cause())
                    }
                }
    }

    private fun initWebServer(vertx: Vertx, tc: VertxTestContext,
                              requestTokenCheckpoint: Checkpoint?,
                              oauthConfirmToken: Checkpoint?,
                              accessTokenCheckpoint: Checkpoint?) {
        val serverStartedCheckpoint = tc.checkpoint()
        val parameters = OAuth1RouterParameters("http://localhost:8083/callback",
                "",
                "",
                consumerKey,
                "bM4bfF9fgmcfutToV17VEDFjQGVECGmIftaUZ3",
                OAuth1TestApi.instance())
        val map = mutableMapOf(Pair(userId, requestTokenSecret))
        val userDataService: UserDataService = mock()
        val authenticationRouter = OAuth1Router(vertx,parameters, userDataService, map).getRouter()

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/oauth-service/request_token").handler { requestToken(it, requestTokenCheckpoint) }
        router.post("/oauth-service/access_token").handler { accessToken(it, accessTokenCheckpoint) }
        router.get("/oauthConfirm").handler { oauthConfirm(it, oauthConfirmToken) }
        router.mountSubRouter("/", authenticationRouter)
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8083) {
                    if (it.succeeded()) {
                        serverStartedCheckpoint.flag()
                    }
                    else {
                        tc.failNow(it.cause())
                    }
                }
    }

    private fun requestToken(routingContext: RoutingContext, checkpoint: Checkpoint?) {
        val authorizationHeader = routingContext.request().getHeader("Authorization")
        assertThat(authorizationHeader).contains("oauth_consumer_key=\"$consumerKey\"")
        checkpoint?.flag()

        val requestTokenInfo = "oauth_token=$requestToken&oauth_token_secret=$requestTokenSecret"
        routingContext.response().end(requestTokenInfo)
    }

    private fun oauthConfirm(routingContext: RoutingContext, checkpoint: Checkpoint?) {
        val code = routingContext.request().getParam("oauth_token")
        val url = routingContext.request().getParam("oauth_callback")
        val expectedUrl = "$redirectUri/$userId"
        assertThat(code).isEqualTo(requestToken)
        assertThat(url).isEqualTo(expectedUrl)
        checkpoint?.flag()
        routingContext.response().end(redirectBody)
    }

    private fun accessToken(routingContext: RoutingContext, checkpoint: Checkpoint?) {
        val authorizationHeader = routingContext.request().getHeader("Authorization")
        assertThat(authorizationHeader).contains("oauth_token=\"$requestToken\"")
        assertThat(authorizationHeader).contains("oauth_verifier=\"$oauthVerifier\"")
        assertThat(authorizationHeader).contains("oauth_consumer_key=\"$consumerKey\"")
        checkpoint?.flag()

        // Return token info
        val tokenInfo = "oauth_token=$accessToken&oauth_token_secret=$accessTokenSecret"
        routingContext.response()
                .end(tokenInfo)
    }
}
