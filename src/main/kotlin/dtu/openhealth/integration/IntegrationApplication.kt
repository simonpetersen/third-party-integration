package dtu.openhealth.integration

import dtu.openhealth.integration.fitbit.FitbitPullService
import dtu.openhealth.integration.fitbit.data.FitbitActivitiesCalories
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.client.WebClient
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.service.impl.HttpServiceImpl
import dtu.openhealth.integration.web.FitbitRestUrl
import dtu.openhealth.integration.web.HttpOAuth2ConnectorClient


class IntegrationApplication

fun main() {
    //val vertx = Vertx.vertx()
    //vertx.deployVerticle(MainVerticle())

    val vertx = Vertx.vertx()
    val webClient = WebClient.create(vertx)
    // BaseUrl = https://api.fitbit.com
    val fitbitUrl = FitbitRestUrl("/1/user/[userId]/activities/calories/date/[date]/1d.json")
    val endpoints = listOf(RestEndpoint(fitbitUrl, FitbitActivitiesCalories.serializer()))
    val httpClient = HttpOAuth2ConnectorClient(webClient)
    val httpService = HttpServiceImpl(httpClient, endpoints)

    val pullingService = FitbitPullService(httpService)
    pullingService.pullData()
}
