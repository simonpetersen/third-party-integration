package dtu.openhealth.integration.garmin.verticle

import dtu.openhealth.integration.garmin.GarminVerticle
import dtu.openhealth.integration.shared.service.mock.MockKafkaProducerService
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.client.WebClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class ActivityEndpointTest {

    val validJsonString ="""
    {
        "activities":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_12345",
                "activityType": "RUNNING",
                "startTimeInSeconds": 1452470400, 
                "startTimeOffsetInSeconds": 0,
                "durationInSeconds": 11580, 
                "averageSpeedInMetersPerSecond": 2.888999938964844, 
                "distanceInMeters": 519818.125, 
                "activeKilocalories": 448,
                "deviceName": "Forerunner 910XT",
                "averagePaceInMinutesPerKilometer": 0.5975272352046997 
            },
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_12346", 
                "activityType": "CYCLING", 
                "startTimeInSeconds": 1452506094, 
                "startTimeOffsetInSeconds": 0, 
                "durationInSeconds": 1824, 
                "averageSpeedInMetersPerSecond": 8.75, 
                "distanceInMeters": 4322.357, 
                "activeKilocalories": 360, 
                "deviceName": "Forerunner 910XT"
            } 
        ]
    }"""


    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        vertx.deployVerticle(GarminVerticle(MockKafkaProducerService()), testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(8082, "localhost", "/api/garmin/activities")
                    .putHeader("Content-Type","application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe { response ->
                        testContext.verify {
                            Assertions.assertThat(response.statusCode()).isEqualTo(200)
                            testContext.completeNow()
                        }
                    }
        })
    }

}
