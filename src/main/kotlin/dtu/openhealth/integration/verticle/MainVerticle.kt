package dtu.openhealth.integration.verticle

import dtu.openhealth.integration.fitbit.FitbitVerticle
import dtu.openhealth.integration.fitbit.data.FitbitActivitiesSummary
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.service.ThirdPartyNotificationService
import dtu.openhealth.integration.service.impl.HttpServiceImpl
import dtu.openhealth.integration.service.impl.TestUserServiceImpl
import dtu.openhealth.integration.web.FitbitRestUrl
import dtu.openhealth.integration.web.HttpOAuth2ConnectorClient
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.web.client.WebClient

class MainVerticle : AbstractVerticle() {

    override fun start() {
        val httpService = HttpServiceImpl(HttpOAuth2ConnectorClient(WebClient.create(vertx)))
        val activityUrl = FitbitRestUrl("/1/user/[userId]/activities/date/[date].json")
        val endpointMap = mapOf(Pair("activities", listOf(RestEndpoint(activityUrl, FitbitActivitiesSummary.serializer()))))
        val notificationService = ThirdPartyNotificationService(httpService, endpointMap, TestUserServiceImpl())

        // Deploy verticles
        vertx.deployVerticle(FitbitVerticle(notificationService))
        vertx.deployVerticle(GarminVerticle())
    }
}