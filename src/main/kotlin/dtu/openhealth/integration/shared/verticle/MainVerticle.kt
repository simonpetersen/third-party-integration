package dtu.openhealth.integration.shared.verticle

import dtu.openhealth.integration.fitbit.FitbitVerticle
import dtu.openhealth.integration.fitbit.data.FitbitActivitiesSummary
import dtu.openhealth.integration.garmin.GarminVerticle
import dtu.openhealth.integration.kafka.publisher.impl.KafkaProducerServiceImpl
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.service.impl.HttpServiceImpl
import dtu.openhealth.integration.shared.service.impl.TestUserServiceImpl
import dtu.openhealth.integration.shared.service.impl.ThirdPartyNotificationServiceImpl
import dtu.openhealth.integration.shared.web.FitbitRestUrl
import dtu.openhealth.integration.shared.web.HttpOAuth2ConnectorClient
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.client.WebClient

class MainVerticle : AbstractVerticle() {

    override fun start() {
        val httpService = HttpServiceImpl(HttpOAuth2ConnectorClient(WebClient.create(vertx)))
        val activityUrl = FitbitRestUrl("/1/user/[userId]/activities/date/[date].json")
        val endpointMap = mapOf(Pair("activities", listOf(RestEndpoint(activityUrl, FitbitActivitiesSummary.serializer()))))
        val notificationService = ThirdPartyNotificationServiceImpl(httpService, endpointMap, TestUserServiceImpl())

        // Setup Kafka Producer
        val kafkaProducerService = KafkaProducerServiceImpl(vertx)

        // Deploy verticles
        vertx.deployVerticle(FitbitVerticle(notificationService))
        vertx.deployVerticle(GarminVerticle(kafkaProducerService))
    }
}
