package dtu.openhealth.integration.shared.service

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.impl.OAuth2TokenRefreshServiceImpl
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.*

@ExtendWith(VertxExtension::class)
class OAuth2TokenRefreshTest {
    private val userId = "user123"
    private val extUserId = "extUser123"
    private val newAccessToken = "accessToken123"
    private val newRefreshToken = "refreshToken123"
    private val oldRefreshToken = "oldRefreshToken123"
    private val clientId = "123a"
    private val clientSecret = "afjsajlsadjf"
    private val oldExpireDateTime = LocalDateTime.now()

    @Test
    fun testRefreshToken(vertx: Vertx, testContext: VertxTestContext) {
        // Checkpoints
        val refreshCalledCheckpoint = testContext.checkpoint()
        val finalCheckpoint = testContext.checkpoint()
        val options = httpServerOptionsOf(
                ssl = true,
                pemKeyCertOptions = pemKeyCertOptionsOf(
                        certPath = "src/test/kotlin/dtu/openhealth/integration/shared/web/server-cert.pem",
                        keyPath = "src/test/kotlin/dtu/openhealth/integration/shared/web/server-key.pem"
                ))

        // Create Web Server
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/oauth2/token").handler { oauth2TokenRefresh(it, testContext, refreshCalledCheckpoint) }
        vertx.createHttpServer(options)
                .requestHandler(router)
                .listen(8183, testContext.succeeding {
                    // Web Server started
                    refreshToken(vertx, testContext, finalCheckpoint)
                })
    }

    private fun refreshToken(vertx: Vertx, context: VertxTestContext, checkpoint: Checkpoint) {
        val clientOptions = webClientOptionsOf(trustAll = true)
        val webClient = WebClient.create(vertx, clientOptions)
        val userService : UserDataService = mock()
        val parameters = OAuth2RefreshParameters("localhost",
                "/oauth2/token/",
                clientId,
                clientSecret,
                8183
        )

        val user = User(userId, extUserId,"someToken", oldRefreshToken, oldExpireDateTime)
        val refreshService = OAuth2TokenRefreshServiceImpl(webClient, parameters, userService)

        GlobalScope.launch {
            val updatedUser = refreshService.refreshToken(user)
            verify(userService).updateTokens(eq(updatedUser))
            context.verify {
                assertThat(updatedUser.token).isEqualTo(newAccessToken)
                assertThat(updatedUser.refreshToken).isEqualTo(newRefreshToken)
                assertThat(updatedUser.expireDateTime).isAfter(oldExpireDateTime)
            }
            checkpoint.flag()
        }
    }

    private fun oauth2TokenRefresh(routingContext: RoutingContext, context: VertxTestContext, checkpoint: Checkpoint) {
        context.verify {
            val authHeader = routingContext.request().getHeader("Authorization")
            val encodedAuthString = Base64.getEncoder()
                    .encodeToString("$clientId:$clientSecret".toByteArray())
            val expectedAuthHeader = "Basic $encodedAuthString"
            assertThat(authHeader).isEqualTo(expectedAuthHeader)

            val formBody = routingContext.bodyAsString
            val grantType = "grant_type=refresh_token"
            val refreshToken = "refresh_token=$oldRefreshToken"
            assertThat(formBody).contains(grantType)
            assertThat(formBody).contains(refreshToken)
        }

        val tokenObject = json {
            obj(
                    "access_token" to newAccessToken,
                    "refresh_token" to newRefreshToken,
                    "user_id" to userId,
                    "expires_in" to 28800
            )
        }

        checkpoint.flag()
        routingContext.response().end(tokenObject.encode())
    }
}
