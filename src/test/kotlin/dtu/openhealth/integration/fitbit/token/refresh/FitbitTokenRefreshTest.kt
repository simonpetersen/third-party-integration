package dtu.openhealth.integration.fitbit.token.refresh

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import dtu.openhealth.integration.fitbit.service.token.refresh.FitbitTokenRefreshServiceImpl
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.token.refresh.AOAuth2TokenRefreshService
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import io.vertx.junit5.Checkpoint
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.http.httpServerOptionsOf
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.core.net.pemKeyCertOptionsOf
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.ext.web.handler.BodyHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.IllegalStateException
import java.time.LocalDateTime
import java.util.*

@ExtendWith(VertxExtension::class)
class FitbitTokenRefreshTest {
    private val port = 8183
    private val userId = "user123"
    private val extUserId = "extUser123"
    private val newAccessToken = "accessToken123"
    private val newRefreshToken = "refreshToken123"
    private val oldRefreshToken = "oldRefreshToken123"
    private val clientId = "123a"
    private val clientSecret = "afjsajlsadjf"
    private val oldExpireDateTime = LocalDateTime.now()
    private val thirdParty = "thirdParty"

    @Test
    fun testRefreshTokenSuccessful(vertx: Vertx, testContext: VertxTestContext)
    {
        startServerAndTestTokenRefresh(vertx, testContext) {
            vx,tc,cp -> refreshTokenSuccessfully(vx, tc, cp)
        }
    }

    @Test
    fun testRefreshTokenInvalidToken(vertx: Vertx, testContext: VertxTestContext)
    {
        startServerAndTestTokenRefresh(vertx, testContext) {
            vx,tc,cp -> refreshTokenInvalidToken(vx, tc, cp)
        }
    }

    private fun startServerAndTestTokenRefresh(vertx: Vertx, testContext: VertxTestContext,
                                               testFunction: (Vertx, VertxTestContext, Checkpoint) -> Unit)
    {
        val refreshCalledCheckpoint = testContext.checkpoint()
        val finalCheckpoint = testContext.checkpoint()
        val options = httpServerOptionsOf(
                ssl = true,
                pemKeyCertOptions = pemKeyCertOptionsOf(
                        certPath = "test-server-cert.pem",
                        keyPath = "test-server-key.pem"
                ))

        // Create Web Server
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/oauth2/token").handler { oauth2TokenRefresh(it, testContext, refreshCalledCheckpoint) }
        vertx.createHttpServer(options)
                .requestHandler(router)
                .listen(8183, testContext.succeeding {
                    // Web Server started
                    testFunction(vertx, testContext, finalCheckpoint)
                })
    }

    private fun refreshTokenInvalidToken(vertx: Vertx, context: VertxTestContext, checkpoint: Checkpoint)
    {
        val userTokenDataService : IUserTokenDataService = mock()
        val refreshService = prepareTokenRefreshService(vertx, userTokenDataService)
        val userToken = UserToken(userId, extUserId,thirdParty,"someToken", "someInvalidToken", oldExpireDateTime)

        refreshService.refreshToken(userToken) {
            context.failNow(IllegalStateException("Refreshing token should have failed"))
        }

        context.verify {
            verify(userTokenDataService,times(0)).updateTokens(any())
        }
        checkpoint.flag()
    }

    private fun refreshTokenSuccessfully(vertx: Vertx, context: VertxTestContext, checkpoint: Checkpoint)
    {
        val userService : IUserTokenDataService = mock()
        val refreshService = prepareTokenRefreshService(vertx, userService)
        val userToken = UserToken(userId, extUserId,thirdParty,"someToken", oldRefreshToken, oldExpireDateTime)

        refreshService.refreshToken(userToken)
        { updatedToken ->
            assertThat(updatedToken).isNotNull
            context.verify {
                assertThat(updatedToken.token).isEqualTo(newAccessToken)
                assertThat(updatedToken.refreshToken).isEqualTo(newRefreshToken)
                assertThat(updatedToken.expireDateTime).isAfter(oldExpireDateTime)
            }
            checkpoint.flag()
        }
    }

    private fun prepareTokenRefreshService(vertx: Vertx, userService: IUserTokenDataService): AOAuth2TokenRefreshService
    {
        val clientOptions = webClientOptionsOf(trustAll = true)
        val webClient = WebClient.create(vertx, clientOptions)
        val parameters = OAuth2RefreshParameters("localhost",
                "/oauth2/token/",
                clientId,
                clientSecret,
                port
        )

        return FitbitTokenRefreshServiceImpl(webClient, parameters, userService)
    }

    private fun oauth2TokenRefresh(routingContext: RoutingContext, context: VertxTestContext, checkpoint: Checkpoint)
    {
        val formBody = routingContext.bodyAsString
        checkpoint.flag()
        context.verify {
            val authHeader = routingContext.request().getHeader("Authorization")
            val encodedAuthString = Base64.getEncoder()
                    .encodeToString("$clientId:$clientSecret".toByteArray())
            val expectedAuthHeader = "Basic $encodedAuthString"
            assertThat(authHeader).isEqualTo(expectedAuthHeader)

            val grantType = "grant_type=refresh_token"
            assertThat(formBody).contains(grantType)
        }

        val refreshToken = "refresh_token=$oldRefreshToken"
        if (!formBody.contains(refreshToken)) {
            routingContext.response().setStatusCode(400).end()
        }

        val tokenObject = json {
            obj(
                    "access_token" to newAccessToken,
                    "refresh_token" to newRefreshToken,
                    "user_id" to userId,
                    "expires_in" to 28800
            )
        }

        routingContext.response().end(tokenObject.encode())
    }
}
