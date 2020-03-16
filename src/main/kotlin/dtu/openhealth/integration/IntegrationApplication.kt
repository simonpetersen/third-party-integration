package dtu.openhealth.integration

import dtu.openhealth.integration.fitbit.FitbitPullingService
import dtu.openhealth.integration.fitbit.data.FitbitActivitiesCalories
import dtu.openhealth.integration.fitbit.mapping.FitbitMapper
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.web.HttpOAuth2Connector

class IntegrationApplication

fun main() {
    //val vertx = Vertx.vertx()
    //vertx.deployVerticle(MainVerticle())

    val mapper = FitbitMapper()
    val endpoints = listOf(RestEndpoint("https://api.fitbit.com/1/user/[userId]/activities/calories/date/[date]/1d.json", FitbitActivitiesCalories.serializer()))
    val httpConnector = HttpOAuth2Connector()

    val pullingService = FitbitPullingService(mapper, endpoints, httpConnector)
    pullingService.pullData()
}
