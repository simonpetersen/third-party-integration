package dtu.openhealth.integration

import dtu.openhealth.integration.fitbit.FitbitRestUrl
import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.fitbit.data.activities.FitbitActivitiesSummary
import dtu.openhealth.integration.fitbit.data.sleep.FitbitSleepLogSummary
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.http.HttpServiceImpl
import dtu.openhealth.integration.shared.service.http.IHttpService
import dtu.openhealth.integration.shared.web.ApiResponse
import dtu.openhealth.integration.shared.web.http.HttpSyncConnectorClient
import dtu.openhealth.integration.shared.web.http.HttpOAuth2ConnectorClient
import io.reactivex.Single
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.core.Vertx
import java.time.LocalDate

class AsyncPerformanceTest
{

    fun runTest()
    {
        val userToken = UserToken("simon", "89NGPS", FitbitConstants.Fitbit, accessToken())
        val endpoints = fitbitEndpoints()
        val parameterList = prepareUserParameterList(userToken)

        syncLegacyTest(userToken, endpoints, parameterList)
        vertxTest(userToken, endpoints, parameterList)
    }

    private fun syncLegacyTest(userToken: UserToken, endpoints: List<RestEndpoint>, parameterList: List<Map<String,String>>)
    {
        val type = "Sync"
        val connectorClient = HttpSyncConnectorClient()
        val httpService = HttpServiceImpl(connectorClient)

        makeCallsAndMeasureTime(userToken, type, endpoints, parameterList, httpService)
    }

    private fun vertxTest(userToken: UserToken, endpoints: List<RestEndpoint>, parameterList: List<Map<String,String>>)
    {
        val type = "Vertx"
        val vertx = Vertx.vertx()
        val connectorClient = HttpOAuth2ConnectorClient(WebClient.create(vertx), 443)
        val httpService = HttpServiceImpl(connectorClient)

        makeCallsAndMeasureTime(userToken, type, endpoints, parameterList, httpService)
    }

    private fun makeCallsAndMeasureTime(userToken: UserToken, type:String, endpoints: List<RestEndpoint>, parameterList: List<Map<String,String>>, httpService: IHttpService)
    {
        val startTime = System.currentTimeMillis()
        val singleLists = parameterList
                .map { httpService.callApiForUser(endpoints, userToken, it) }

        val responseList = Single.zip(singleLists) {
            it.filterIsInstance<List<ApiResponse>>()
        }

        responseList.subscribe(
                { success ->
                    val endTime = System.currentTimeMillis()
                    println("$type calls took ${endTime - startTime} ms. Result = $success")
                },
                { error -> println(error) }
        )
    }

    private fun fitbitEndpoints() : List<RestEndpoint>
    {
        val activityUrl = FitbitRestUrl("/1/user/[userId]/activities/date/[date].json")
        val sleepUrl = FitbitRestUrl("/1.2/user/[userId]/sleep/date/[date].json")

        return listOf(
                RestEndpoint(activityUrl, FitbitActivitiesSummary.serializer()),
                RestEndpoint(sleepUrl, FitbitSleepLogSummary.serializer())
        )
    }

    private  fun prepareUserParameterList(userToken: UserToken): List<Map<String, String>>
    {
        val date = LocalDate.now()
        val parameterList = mutableListOf<Map<String,String>>()

        for (i in 1..5) {
            parameterList.add(parametersAtDate(userToken, date))
            date.minusDays(1)
        }

        return parameterList
    }

    private fun parametersAtDate(userToken: UserToken, date: LocalDate): Map<String,String>
    {
        return mapOf(
                Pair("userId", userToken.extUserId),
                Pair("date", date.toString())
        )
    }

    private fun accessToken(): String
    {
        return "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMkJSNjYiLCJzdWIiOiI4OU5HUFMiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJyYWN0IHJzZXQgcndlaSByaHIgcnBybyBybnV0IHJzbGUiLCJleHAiOjE1OTA2Nzc3NDQsImlhdCI6MTU5MDY0ODk0NH0.oqe-V6v-Nv2BjAxv5KlQP4OoX2V3CxBPWBpbnUVlqCM"
    }
}