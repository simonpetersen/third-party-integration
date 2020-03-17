package dtu.openhealth.integration

import dtu.openhealth.integration.fitbit.FitbitPullingService
import dtu.openhealth.integration.fitbit.data.FitbitActivitiesCalories
import dtu.openhealth.integration.fitbit.mapping.FitbitMapper
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.service.impl.HttpServiceImpl
import dtu.openhealth.integration.web.HttpOAuth2ConnectorClient

class IntegrationApplication

fun main() {
    //val vertx = Vertx.vertx()
    //vertx.deployVerticle(MainVerticle())

    val mapper = FitbitMapper()
    val endpoints = listOf(RestEndpoint("https://api.fitbit.com/1/user/[userId]/activities/calories/date/[date]/1d.json", FitbitActivitiesCalories.serializer()))
    val httpClient = HttpOAuth2ConnectorClient()
    val httpService = HttpServiceImpl(httpClient, endpoints)

    val pullingService = FitbitPullingService(mapper, httpService)
    pullingService.pullData()
}
