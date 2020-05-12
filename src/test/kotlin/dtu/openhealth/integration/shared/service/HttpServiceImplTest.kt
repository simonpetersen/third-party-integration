package dtu.openhealth.integration.shared.service

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.shared.model.ThirdPartyData
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.impl.HttpServiceImpl
import dtu.openhealth.integration.shared.service.mock.MockRestUrl
import dtu.openhealth.integration.shared.web.ApiRequest
import dtu.openhealth.integration.shared.web.ApiResponse
import dtu.openhealth.integration.shared.web.HttpConnectorClient
import io.reactivex.Single
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail

@ExtendWith(VertxExtension::class)
class HttpServiceImplTest {
    private val userId = "testUser3435"
    private val extUserId = "HEGD-3874"

    @Test
    fun testCallApiForUser(vertx: Vertx, testContext: VertxTestContext) {
        // Test activity endpoint
        val activitiesTestUri = "/data/activities"
        val activityEndpoint = RestEndpoint(MockRestUrl(activitiesTestUri), ThirdPartyData.serializer())
        val activitiesRequest = ApiRequest(activityEndpoint, activitiesTestUri, emptyMap())
        val activitiesResponseJson = "{activities: {userId: $extUserId}}"
        val activitiesResponse = ApiResponse(activitiesResponseJson, ThirdPartyData.serializer(), emptyMap())

        // Test sleep endpoint
        val sleepTestUri = "/data/sleep"
        val sleepEndpoint = RestEndpoint(MockRestUrl(sleepTestUri), ThirdPartyData.serializer())
        val sleepRequest = ApiRequest(sleepEndpoint, sleepTestUri, emptyMap())
        val sleepResponseJson = "{sleep: {userId: $extUserId}}"
        val sleepResponse = ApiResponse(sleepResponseJson, ThirdPartyData.serializer(), emptyMap())
        val user = UserToken(userId, extUserId, "testToken")

        // Mock
        val numberOfEndpoints = 2
        val httpClient: HttpConnectorClient = mock()
        val endpoints = listOf(activityEndpoint, sleepEndpoint)
        whenever(httpClient.get(eq(activitiesRequest), eq(user))).thenReturn(Single.just(activitiesResponse))
        whenever(httpClient.get(eq(sleepRequest), eq(user))).thenReturn(Single.just(sleepResponse))
        val httpService = HttpServiceImpl(httpClient)

        val singleResponseList = httpService.callApiForUser(endpoints, user, mapOf())

        singleResponseList.subscribe(
                { result ->
                    testContext.verify {
                        assertThat(result.size).isEqualTo(numberOfEndpoints)
                        assertThat(result).contains(activitiesResponse)
                        assertThat(result).contains(sleepResponse)
                    }
                    testContext.completeNow()
                },
                { error ->
                    testContext.failNow(error)
                })
    }

    @Test
    fun testCallApiForUserWithParameters(vertx: Vertx, testContext: VertxTestContext) {
        val numberOfEndpoints = 2
        val parameters = mapOf(Pair("userId", extUserId))
        val baseActivitiesTestUri = "/data/[userId]/activities"
        val activitiesTestUri = "/data/$extUserId/activities"
        val activitiesTestEndpoint = RestEndpoint(MockRestUrl(baseActivitiesTestUri), ThirdPartyData.serializer())
        val activitiesRequest = ApiRequest(activitiesTestEndpoint, activitiesTestUri, parameters)
        val activitiesResponseJson = "{activities: {userId: $extUserId}}"
        val activitiesResponse = ApiResponse(activitiesResponseJson, ThirdPartyData.serializer(), emptyMap())

        val baseSleepTestUri = "/data/[userId]/sleep"
        val sleepTestUri = "/data/$extUserId/sleep"
        val sleepTestEndpoint = RestEndpoint(MockRestUrl(baseSleepTestUri), ThirdPartyData.serializer())
        val sleepRequest = ApiRequest(sleepTestEndpoint, sleepTestUri, parameters)
        val sleepResponseJson = "{sleep: {userId: $extUserId}}"
        val sleepResponse = ApiResponse(sleepResponseJson, ThirdPartyData.serializer(), emptyMap())
        val userToken = UserToken(userId, extUserId, "testToken")

        // Mock
        val httpClient: HttpConnectorClient = mock()
        val endpoints = listOf(activitiesTestEndpoint, sleepTestEndpoint)
        whenever(httpClient.get(eq(activitiesRequest), eq(userToken))).thenReturn(Single.just(activitiesResponse))
        whenever(httpClient.get(eq(sleepRequest), eq(userToken))).thenReturn(Single.just(sleepResponse))
        val httpService = HttpServiceImpl(httpClient)

        val singleResponseList = httpService.callApiForUser(endpoints, userToken, parameters)

        singleResponseList.subscribe(
                { result ->
                    testContext.verify {
                        assertThat(result.size).isEqualTo(numberOfEndpoints)
                        assertThat(result).contains(activitiesResponse)
                        assertThat(result).contains(sleepResponse)
                    }
                    testContext.completeNow()
                },
                { error -> testContext.failNow(error) }
        )
    }
}