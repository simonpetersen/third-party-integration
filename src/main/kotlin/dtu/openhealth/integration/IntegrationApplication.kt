package dtu.openhealth.integration

import dtu.openhealth.integration.fitbit.FitbitPullService
import dtu.openhealth.integration.fitbit.data.FitbitActivitiesSummary
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.client.WebClient
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.service.ThirdPartyNotificationService
import dtu.openhealth.integration.service.impl.HttpServiceImpl
import dtu.openhealth.integration.service.impl.TestUserServiceImpl
import dtu.openhealth.integration.verticle.MainVerticle
import dtu.openhealth.integration.web.FitbitRestUrl
import dtu.openhealth.integration.web.HttpOAuth2ConnectorClient


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
