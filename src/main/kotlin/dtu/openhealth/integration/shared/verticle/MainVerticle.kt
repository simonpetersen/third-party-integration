package dtu.openhealth.integration.shared.verticle

import dtu.openhealth.integration.fitbit.FitbitVerticle
import dtu.openhealth.integration.fitbit.data.FitbitActivitiesSummary
import dtu.openhealth.integration.garmin.GarminVerticle
import dtu.openhealth.integration.kafka.producer.impl.KafkaProducerServiceImpl
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.service.impl.*
import dtu.openhealth.integration.shared.web.FitbitRestUrl
import dtu.openhealth.integration.shared.web.HttpOAuth2ConnectorClient
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.client.WebClient

class MainVerticle : AbstractVerticle() {

    override fun start() {
        val httpService = HttpServiceImpl(HttpOAuth2ConnectorClient(WebClient.create(vertx)))
        val activityUrl = FitbitRestUrl("/1/user/[ownerId]/activities/date/[date].json")
        val endpointMap = mapOf(Pair("activities", listOf(RestEndpoint(activityUrl, FitbitActivitiesSummary.serializer()))))
        val userDataService = VertxUserServiceImpl(vertx.delegate)
        val refreshParameters = OAuth2RefreshParameters(host = "api.fitbit.com",
                refreshPath = "/oauth2/token",
                clientId = "123",
                clientSecret = "abc"
        )
        val tokenRefreshService = OAuth2TokenRefreshServiceImpl(WebClient.create(vertx), refreshParameters, userDataService)
        val notificationService = ThirdPartyNotificationServiceImpl(httpService, endpointMap, userDataService, tokenRefreshService)

        // Setup Kafka Producer
        val kafkaProducerService = KafkaProducerServiceImpl(vertx)

        // Deploy verticles
        vertx.deployVerticle(FitbitVerticle(notificationService))
        vertx.deployVerticle(GarminVerticle(kafkaProducerService))
    }
}
