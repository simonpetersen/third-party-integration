package dtu.openhealth.integration.garmin.verticle

import dtu.openhealth.integration.garmin.GarminVerticle
import dtu.openhealth.integration.shared.service.mock.MockKafkaProducerService
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.client.WebClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(VertxExtension::class)
class ThirdPartyEndpointTest {

    private val validJsonString = """
    {
        "thirdparty":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_67891", 
                "activityType": "WALKING", 
                "activeKilocalories": 1136, 
                "bmrKilocalories": 1736, 
                "steps": 11467, 
                "distanceInMeters": 14001.0, 
                "durationInSeconds": 86400, 
                "activeTimeInSeconds": 4680,
                "startTimeInSeconds": 1472688000, 
                "startTimeOffsetInSeconds": 0, 
                "floorsClimbed": 12,
                "source": "FITBIT"
            }, 
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2",    
                "summaryId": "EXAMPLE_67892", 
                "activityType": "WALKING", 
                "activeKilocalories": 1708, 
                "bmrKilocalories": 1200, 
                "steps": 13986, 
                "distanceInMeters": 17091.0, 
                "durationInSeconds": 86400, 
                "activeTimeInSeconds": 8340, 
                "startTimeInSeconds": 1472774400, 
                "startTimeOffsetInSeconds": 0, 
                "floorsClimbed": 42,
                "source": "FITBIT" 
            }
        ]
    }"""


    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        vertx.deployVerticle(GarminVerticle(MockKafkaProducerService()), testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(8084, "localhost", "/api/garmin/thirdparty")
                    .putHeader("Content-Type","application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe { response ->
                        testContext.verify {
                            assertThat(response.statusCode()).isEqualTo(200)
                            testContext.completeNow()
                        }
                    }
        })
    }
}
