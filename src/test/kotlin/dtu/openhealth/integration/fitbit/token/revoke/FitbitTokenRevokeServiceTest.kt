package dtu.openhealth.integration.fitbit.token.revoke

import com.nhaarman.mockitokotlin2.mock
import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.fitbit.service.token.revoke.FitbitTokenRevokeService
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.token.refresh.ITokenRefreshService
import dtu.openhealth.integration.shared.service.token.revoke.data.OAuth2RevokeParameters
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
class FitbitTokenRevokeServiceTest {
    private val port = 8677
    private val userId = "USER-TEST-123"
    private val userToken = "token2354"
    private val refreshToken = "refresh-AjhfdkD2"
    private val revokeUri = "/revoke"
    private val subscriptionUri = "/subscription/[${FitbitConstants.SubscriptionId}]"
    private val clientId = "hkdalfhke"
    private val clientSecret = "klaj23fewl-FfgfEfef"

    @Test
    fun testRevokeOAuth2(vertx: Vertx, tc: VertxTestContext)
    {
        val tokenRevokeCheckpoint = tc.checkpoint()
        val subscriptionDeleteCheckpoint = tc.checkpoint()
        val successfulResponseCheckpoint = tc.checkpoint()
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post(revokeUri).handler { handleRevokeToken(it, tc, tokenRevokeCheckpoint) }
        router.delete("/subscription/:id").handler { handleDeleteSubscription(it, tc, subscriptionDeleteCheckpoint) }
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, tc.succeeding {
                    testFunction(vertx, tc, successfulResponseCheckpoint)
                })
    }

    private fun testFunction(vertx: Vertx, tc: VertxTestContext, checkpoint: Checkpoint)
    {
        val userToken = UserToken(userId, "extUser123", FitbitConstants.Fitbit, userToken, refreshToken = refreshToken)
        val revokeParameters = revokeParameters()
        val webClient = WebClient.create(vertx)
        val refreshService : ITokenRefreshService = mock()
        val revokeService = FitbitTokenRevokeService(webClient, revokeParameters, refreshService)
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

    private fun handleRevokeToken(routingContext: RoutingContext, tc: VertxTestContext, checkpoint: Checkpoint)
    {
        tc.verify {
            val body = routingContext.bodyAsString
            val expectedToken = "token=$refreshToken"
            assertThat(body).contains(expectedToken)
        }
        checkpoint.flag()
        routingContext.response().setStatusCode(204).end()
    }

    private fun handleDeleteSubscription(routingContext: RoutingContext, tc: VertxTestContext, checkpoint: Checkpoint)
    {
        tc.verify {
            val id = routingContext.request().getParam("id")
            assertThat(id).isEqualTo(userId)
        }
        checkpoint.flag()
        routingContext.response().setStatusCode(204).end()
    }

    private fun revokeParameters(): OAuth2RevokeParameters
    {
        return OAuth2RevokeParameters(
                "localhost",
                revokeUri,
                subscriptionUri,
                clientId,
                clientSecret,
                port,
                ssl = false
        )
    }
}