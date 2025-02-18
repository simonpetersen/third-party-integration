package dtu.openhealth.integration.garmin.verticle

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.garmin.GarminRouter
import dtu.openhealth.integration.garmin.data.respiration.RespirationSummaryGarmin
import dtu.openhealth.integration.shared.service.push.IThirdPartyPushService
import dtu.openhealth.integration.shared.web.auth.IAuthorisationRouter
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.client.WebClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class RespirationsEndpointTest {

    private val port = 8184
    private val validJsonString ="""
    {
        "allDayRespiration":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "x15372ea-5d7866b4", 
                "startTimeInSeconds": 1568171700, 
                "durationInSeconds": 900, 
                "startTimeOffsetInSeconds": -18000, 
                "timeOffsetEpochToBreaths": {
                    "0": 14.63, 
                    "60": 14.4, 
                    "120": 14.38, 
                    "180": 14.38, 
                    "300": 17.1, 
                    "540": 16.61, 
                    "600": 16.14, 
                    "660": 14.59, 
                    "720": 14.65, 
                    "780": 15.09, 
                    "840": 14.88
                }
            },
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "x15372ea-5d786a38", 
                "startTimeInSeconds": 1568172600, 
                "durationInSeconds": 900, 
                "startTimeOffsetInSeconds": -18000, 
                "timeOffsetEpochToBreaths": {
                    "0": 14.82, 
                    "60": 16.58, 
                    "120": 13.2, 
                    "180": 14.69, 
                    "240": 16.17, 
                    "300": 16.04, 
                    "360": 15.03, 
                    "420": 14.57, 
                    "480": 14.41, 
                    "540": 13.82, 
                    "600": 13.26, 
                    "660": 12.76, 
                    "780": 13.3, 
                    "840": 13.53
                } 
            }
        ]
    }"""

    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext)
    {
        val thirdPartyPushService : IThirdPartyPushService = mock()
        val authRouter : IAuthorisationRouter = mock()
        whenever(authRouter.getRouter()).thenReturn(Router.router(vertx))

        val garminRouter = GarminRouter(vertx, thirdPartyPushService, authRouter)
        vertx.createHttpServer().requestHandler(garminRouter.getRouter()).listen(port, testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(port, "localhost", "/respirations")
                    .putHeader("Content-Type","application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe(
                            { response ->
                                testContext.verify {
                                    assertThat(response.statusCode()).isEqualTo(200)
                                    verify(thirdPartyPushService, times(2))
                                            .saveDataToOMH(any<RespirationSummaryGarmin>())
                                    testContext.completeNow()
                                }
                            },
                            { error -> testContext.failNow(error)}
                    )
        })
    }

}
