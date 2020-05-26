package dtu.openhealth.integration.shared.service.token.revoke

import dtu.openhealth.integration.garmin.auth.OAuth1TestApi
import dtu.openhealth.integration.garmin.data.GarminConstants
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.token.revoke.data.OAuth1RevokeParameters
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
class OAuth1TokenRevokeServiceTest {

    private val port = 8676
    private val user = "USER-ABC-123"
    private val userToken = "token2354"
    private val tokenSecret = "secretAjhfdkD2"
    private val revokeUri = "/revoke"
    private val consumerKey = "hkdalfhke"
    private val consumerSecret = "klaj23fewl-FfgfEfef"

    @Test
    fun testRevokeOAuth1(vertx: Vertx, tc: VertxTestContext)
    {
        val tokenDeleteCheckpoint = tc.checkpoint()
        val successfulResponseCheckpoint = tc.checkpoint()
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.delete(revokeUri).handler { handleDeleteToken(it, tokenDeleteCheckpoint) }
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, tc.succeeding {
                    testFunction(vertx, tc, successfulResponseCheckpoint)
                })
    }

    private fun testFunction(vertx: Vertx, tc: VertxTestContext, checkpoint: Checkpoint)
    {
        val userToken = UserToken(user, "extUser123", GarminConstants.Garmin, userToken, tokenSecret = tokenSecret)
        val revokeParameters = revokeParameters()
        val webClient = WebClient.create(vertx)
        val revokeService = OAuth1TokenRevokeService(webClient, revokeParameters)
        val response = revokeService.revokeToken(userToken)

        response.subscribe(
                { res ->
                    tc.verify {
                        assertThat(res.statusCode).isEqualTo(204)
                    }
                    checkpoint.flag()
                },
                { err ->
                    tc.failNow(err)
                }
        )
    }

    private fun handleDeleteToken(routingContext: RoutingContext, checkpoint: Checkpoint)
    {
        checkpoint.flag()
        routingContext.response().setStatusCode(204).end()
    }

    private fun revokeParameters(): OAuth1RevokeParameters
    {
        return OAuth1RevokeParameters(
                "localhost",
                revokeUri,
                consumerKey,
                consumerSecret,
                port,
                OAuth1TestApi(port),
                ssl = false
        )
    }
}