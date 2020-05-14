package dtu.openhealth.integration.shared.web.auth

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.UserDataService
import dtu.openhealth.integration.shared.web.parameters.OAuth1RouterParameters
import io.vertx.junit5.Checkpoint
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.ext.web.handler.BodyHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class OAuth1RouterTest {
    // Values
    private val port = 8198
    private val oauthServicePort = port + 1
    private val userId = "testUser5756"
    private val consumerKey = "22d54322-2d2h-g67c-9876-234rgf264567"
    private val consumerSecret = "bM4bfF9fgmcfutToV17VEDFdfdsGVECGmIftaUZ3"
    private val authCode = "6789efgh"
    private val oauthVerifier = "verifier_$authCode"
    private val requestToken = "request_token_sfsfds_$authCode"
    private val requestTokenSecret = "request_token_secret_dfssdfs_$authCode"
    private val accessToken = "access_token_$authCode"
    private val accessTokenSecret = "access_token_secret_$authCode"

    // Urls and bodies
    private val callbackUri = "https://localhost:$port/callback"
    private val returnUri = "http://localhost:$oauthServicePort/return"
    private val redirectBody = "Redirect to localhost/oauthConfirm"
    private val returnBody = "Authorization finished"

    @Test
    fun testOAuth1RouterRedirect(vertx: Vertx, tc: VertxTestContext) {
        val checkpoint = tc.checkpoint()
        val requestTokenCheckpoint = tc.checkpoint()
        val oauthConfirmToken = tc.checkpoint()
        val userDataService: UserDataService = mock()
        initWebServers(vertx, tc, userDataService, requestTokenCheckpoint, oauthConfirmToken,null)

        val client = WebClient.create(vertx)
        client.get(port, "localhost", "/auth/$userId")
                .send { ar ->
                    if (ar.succeeded()) {
                        validateAuthorizationRedirect(tc, ar.result(), checkpoint)
                    }
                    else {
                        tc.failNow(ar.cause())
                    }
                }
    }

    @Test
    fun testOAuth1AccessToken(vertx: Vertx, tc: VertxTestContext) {
        val accessTokenCheckpoint = tc.checkpoint()
        val userDataService: UserDataService = mock()
        initWebServers(vertx, tc, userDataService,null,null, accessTokenCheckpoint)

        val requestUri = "/callback/$userId?oauth_token=$requestToken&oauth_verifier=$oauthVerifier"
        val checkpoint = tc.checkpoint()
        val client = WebClient.create(vertx)
        client.get(port, "localhost", requestUri)
                .send { ar ->
                    if (ar.succeeded()) {
                        validateAccessTokenObtained(tc, userDataService, checkpoint, ar.result())
                    }
                    else {
                        tc.failNow(ar.cause())
                    }
                }
    }

    private fun initWebServers(vertx: Vertx, tc: VertxTestContext, userDataService: UserDataService,
                               requestTokenCheckpoint: Checkpoint?,
                               oauthConfirmToken: Checkpoint?,
                               accessTokenCheckpoint: Checkpoint?) {
        val serverStartedCheckpoint = tc.checkpoint(2)
        val parameters = OAuth1RouterParameters(callbackUri, returnUri,
                consumerKey, consumerSecret,
                OAuth1TestApi(oauthServicePort))
        val map = mutableMapOf(Pair(userId, requestTokenSecret))
        val authRouter = OAuth1Router(vertx, parameters, userDataService, map).getRouter()

        val oauthServiceRouter = Router.router(vertx)
        oauthServiceRouter.route().handler(BodyHandler.create())
        oauthServiceRouter.post("/oauth-service/request_token").handler { requestToken(it, requestTokenCheckpoint) }
        oauthServiceRouter.post("/oauth-service/access_token").handler { accessToken(it, accessTokenCheckpoint) }
        oauthServiceRouter.get("/oauthConfirm").handler { oauthConfirm(it, oauthConfirmToken) }
        oauthServiceRouter.get("/return").handler { returnHandler(it) }

        createWebServer(vertx, tc, oauthServiceRouter, oauthServicePort, serverStartedCheckpoint)
        createWebServer(vertx, tc, authRouter, port, serverStartedCheckpoint)
    }

    private fun createWebServer(vertx: Vertx, tc: VertxTestContext, router: Router, port: Int, checkpoint: Checkpoint) {
        vertx.createHttpServer().requestHandler(router).listen(port) {
            if (it.succeeded()) { checkpoint.flag() }
            else { tc.failNow(it.cause()) }
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
        val expectedUrl = "$callbackUri/$userId"
        assertThat(code).isEqualTo(requestToken)
        assertThat(url).isEqualTo(expectedUrl)
        checkpoint?.flag()
        routingContext.response().end(redirectBody)
    }

    private fun returnHandler(routingContext: RoutingContext) {
        routingContext.response().end(returnBody)
    }

    private fun accessToken(routingContext: RoutingContext, checkpoint: Checkpoint?) {
        val authorizationHeader = routingContext.request().getHeader("Authorization")
        assertThat(authorizationHeader).contains("oauth_token=\"$requestToken\"")
        assertThat(authorizationHeader).contains("oauth_verifier=\"$oauthVerifier\"")
        assertThat(authorizationHeader).contains("oauth_consumer_key=\"$consumerKey\"")
        checkpoint?.flag()

        val tokenInfo = "oauth_token=$accessToken&oauth_token_secret=$accessTokenSecret"
        routingContext.response().end(tokenInfo)
    }

    private fun validateAccessTokenObtained(tc: VertxTestContext, userDataService: UserDataService,
                                            checkpoint: Checkpoint, response: HttpResponse<Buffer>) {
        tc.verify {
            assertThat(response.statusCode()).isEqualTo(200)
            assertThat(response.bodyAsString()).isEqualTo(returnBody)
            val expectedToken = UserToken(userId, accessToken, accessToken, tokenSecret = accessTokenSecret)
            verify(userDataService).insertUser(eq(expectedToken))
        }
        checkpoint.flag()
    }

    private fun validateAuthorizationRedirect(tc: VertxTestContext, response: HttpResponse<Buffer>,
                                              checkpoint: Checkpoint) {
        tc.verify {
            assertThat(response.statusCode()).isEqualTo(200)
            assertThat(response.body().toString()).isEqualTo(redirectBody)
        }
        checkpoint.flag()
    }
}