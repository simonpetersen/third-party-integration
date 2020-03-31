package dtu.openhealth.integration

import io.vertx.reactivex.core.Vertx
import dtu.openhealth.integration.shared.verticle.MainVerticle


class IntegrationApplication

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())

    /*val webClient = WebClient.create(vertx)
    // BaseUrl = https://api.fitbit.com
    val activityUrl = FitbitRestUrl("/1/user/[userId]/activities/date/[date].json")

    val endpoints = listOf(RestEndpoint(activityUrl, FitbitActivitiesSummary.serializer()),
        RestEndpoint(activityUrl, FitbitActivitiesSummary.serializer()))
    val httpClient = HttpOAuth2ConnectorClient(webClient)
    val httpService = HttpServiceImpl(httpClient)

    //val endpointMap = mapOf(Pair("activities", endpoints))
    //val notificationService = ThirdPartyNotificationService(httpService, endpointMap, TestUserServiceImpl())
    val pullingService = FitbitPullService(httpService, endpoints, TestUserServiceImpl())
    pullingService.pullData()

     */
}
