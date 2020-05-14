package dtu.openhealth.integration.garmin.verticle

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dtu.openhealth.integration.garmin.GarminRouter
import dtu.openhealth.integration.garmin.data.PulseOXSummaryGarmin
import dtu.openhealth.integration.shared.service.ThirdPartyPushService
import dtu.openhealth.integration.shared.web.auth.IAuthorizationRouter
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.client.WebClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class PulseEndpointTest {

    private val port = 8184
    private val validJsonString = """
    {
        "pulseOX":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": " EXAMPLE_34343", 
                "calendarDate": "2018-08-27", 
                "startTimeInSeconds": 1535400706, 
                "durationInSeconds": 86400, 
                "startTimeOffsetInSeconds": 7200, 
                "timeOffsetSpo2Values": {
                    "0": 98,
                    "3600": 95,
                    "7200": 93,
                    "10800": 98
                }
            }
        ]
    }"""


    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        val thirdPartyPushService : ThirdPartyPushService = mock()
        val authRouter: IAuthorizationRouter = mock()
        whenever(authRouter.getRouter()).thenReturn(Router.router(vertx))

        val garminRouter = GarminRouter(vertx, thirdPartyPushService, authRouter)
        vertx.createHttpServer().requestHandler(garminRouter.getRouter()).listen(port, testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(port, "localhost", "/pulse")
                    .putHeader("Content-Type", "application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe(
                            { response ->
                                testContext.verify {
                                    Assertions.assertThat(response.statusCode()).isEqualTo(200)
                                    verify(thirdPartyPushService).saveDataToOMH(any<PulseOXSummaryGarmin>())
                                    testContext.completeNow()
                                }
                            },
                            { error -> testContext.failNow(error)}
                    )
        })
    }

}
