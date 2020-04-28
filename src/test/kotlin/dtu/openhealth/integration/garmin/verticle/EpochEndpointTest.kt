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
class EpochEndpointTest {

    private val validJsonString = """
    {
        "epochs":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_1234", 
                "activityType": "SEDENTARY", 
                "activeKilocalories": 0, 
                "steps": 0, 
                "distanceInMeters": 0.0, 
                "durationInSeconds": 900, 
                "activeTimeInSeconds": 600, 
                "met": 1.0,
                "intensity": "SEDENTARY",
                "startTimeInSeconds": 1454418900,
                "startTimeOffsetInSeconds": 3600
            },
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_5678", 
                "activityType": "RUNNING", 
                "activeKilocalories": 257, 
                "steps": 427, 
                "distanceInMeters": 222.07, 
                "durationInSeconds": 900, 
                "activeTimeInSeconds": 300, 
                "met": 9.894117,
                "intensity": "HIGHLY_ACTIVE", 
                "startTimeInSeconds": 1454418900, 
                "startTimeOffsetInSeconds": 3600
            }
        ]
    }"""


    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        vertx.deployVerticle(GarminVerticle(MockKafkaProducerService()), testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(8084, "localhost", "/api/garmin/epochs")
                    .putHeader("Content-Type", "application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe { response ->
                        testContext.verify {
                            assertThat(response.statusCode()).isEqualTo(200)
                            testContext.completeNow()
                        }
                        testContext.awaitCompletion(2000, TimeUnit.SECONDS)
                    }
        })
    }
}
