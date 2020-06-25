package dtu.openhealth.integration.fitbit.service.pull

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.http.HttpServiceImpl
import dtu.openhealth.integration.shared.service.mock.LocalhostRestUrl
import dtu.openhealth.integration.shared.service.token.refresh.ITokenRefreshService
import dtu.openhealth.integration.shared.web.http.HttpOAuth2ConnectorClient
import io.vertx.junit5.Checkpoint
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.http.httpServerOptionsOf
import io.vertx.kotlin.core.net.pemKeyCertOptionsOf
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.client.WebClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class FitbitPullServiceTest {
    private val userId = "fitbit_test_user_2"
    private val extUserId = "fitbit_id_2"
    private val token = "fitbit_token_2"
    private val port = 8187
    private val requiredNumberOfPasses = 6
    private val jsonBody = "{ \"userId\": \"$extUserId\", \"value\": 17 }"

    @Test
    fun testFitbitDataPull(vertx: Vertx, tc: VertxTestContext)
    {
        val activityCheckpoint = tc.checkpoint(requiredNumberOfPasses)
        val sleepCheckpoint = tc.checkpoint(requiredNumberOfPasses)
        val router = Router.router(vertx)
        router.get("/:userId/activity/:date").handler { activity(it, tc, activityCheckpoint) }
        router.get("/:userId/sleep/:date").handler { sleep(it, tc, sleepCheckpoint) }

        val options = httpServerOptionsOf(
                ssl = true,
                pemKeyCertOptions = pemKeyCertOptionsOf(
                        certPath = "test-server-cert.pem",
                        keyPath = "test-server-key.pem"
                ))

        vertx.createHttpServer(options)
                .requestHandler(router)
                .listen(port, tc.succeeding {
                    runPullTest(vertx)
                })
    }

    private fun runPullTest(vertx: Vertx)
    {
        GlobalScope.launch(vertx.delegate.dispatcher()) {
            val clientOptions = webClientOptionsOf(trustAll = true)
            val webClient = WebClient.create(vertx, clientOptions)
            val httpClient = HttpOAuth2ConnectorClient(webClient, port)
            val httpService = HttpServiceImpl(httpClient)
            val kafkaProducerService: IKafkaProducerService = mock()
            val tokenRefreshService: ITokenRefreshService = mock()
            val userTokenDataService: IUserTokenDataService = mock()
            whenever(userTokenDataService.getTokensFromThirdParty(FitbitConstants.Fitbit))
                    .thenReturn(initUserTokenList())

            val fitbitPullService = FitbitPullService(httpService, endpointList(), kafkaProducerService, tokenRefreshService, userTokenDataService)
            fitbitPullService.pullData()
        }
    }

    private fun activity(routingContext: RoutingContext, tc: VertxTestContext, checkpoint: Checkpoint)
    {
        validateRoutingParameters(routingContext, tc)
        checkpoint.flag()
        routingContext.response().end(jsonBody)
    }

    private fun sleep(routingContext: RoutingContext, tc: VertxTestContext, checkpoint: Checkpoint)
    {
        validateRoutingParameters(routingContext, tc)
        checkpoint.flag()
        routingContext.response().end(jsonBody)
    }

    private fun validateRoutingParameters(routingContext: RoutingContext, tc: VertxTestContext)
    {
        val userId = routingContext.request().getParam("userId")
        val date = routingContext.request().getParam("date")
        tc.verify {
            assertThat(userId).isEqualTo(extUserId)
            assertThat(date).isNotNull()
        }
    }

    private fun initUserTokenList(): List<UserToken>
    {
        return listOf(UserToken(userId, extUserId, FitbitConstants.Fitbit, token))
    }

    private fun endpointList(): List<RestEndpoint>
    {
        val activityUrl = LocalhostRestUrl("/[userId]/activity/[date]")
        val activityEndpoint = RestEndpoint(activityUrl, FitbitTestDataType.serializer())
        val sleepUrl = LocalhostRestUrl("/[userId]/sleep/[date]")
        val sleepEndpoint = RestEndpoint(sleepUrl, FitbitTestDataType.serializer())

        return listOf(activityEndpoint, sleepEndpoint)
    }
}