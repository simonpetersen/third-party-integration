package dtu.openhealth.integration

import dtu.openhealth.integration.kafka.consumer.KafkaConsumer
import dtu.openhealth.integration.shared.verticle.MainVerticle
import io.vertx.reactivex.core.Vertx


class IntegrationApplication

fun main() {
    val vertx = Vertx.vertx()
    val kafkaConsumer = KafkaConsumer(vertx)
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
