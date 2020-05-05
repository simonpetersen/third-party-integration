package dtu.openhealth.integration.shared.verticle

import dtu.openhealth.integration.fitbit.FitbitOAuth2Router
import dtu.openhealth.integration.fitbit.FitbitVerticle
import dtu.openhealth.integration.fitbit.data.*
import dtu.openhealth.integration.garmin.GarminVerticle
import dtu.openhealth.integration.kafka.producer.KafkaProducerService
import dtu.openhealth.integration.kafka.producer.impl.KafkaProducerServiceImpl
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.service.UserDataService
import dtu.openhealth.integration.shared.service.impl.*
import dtu.openhealth.integration.shared.web.FitbitRestUrl
import dtu.openhealth.integration.shared.web.HttpOAuth2ConnectorClient
import dtu.openhealth.integration.shared.web.auth.GarminApi
import dtu.openhealth.integration.shared.web.auth.OAuth1Router
import dtu.openhealth.integration.shared.web.parameters.OAuth1RouterParameters
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.kotlin.ext.auth.oauth2.oAuth2ClientOptionsOf
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import io.vertx.reactivex.ext.web.client.WebClient

class MainVerticle : AbstractVerticle() {

    override fun start() {
        val userDataService = UserDataServiceImpl(vertx.delegate)
        val kafkaProducerService = KafkaProducerServiceImpl(vertx)

        // Deploy verticles
        vertx.deployVerticle(initFitbitVerticle(userDataService, kafkaProducerService))
        vertx.deployVerticle(initGarminVerticle(userDataService, kafkaProducerService))
        vertx.deployVerticle(OmhConsumerVerticle(userDataService))
    }

    private fun initGarminVerticle(userDataService: UserDataService, kafkaProducerService: KafkaProducerService): GarminVerticle {
        val consumerKey = "123"
        val consumerSecret = "123"
        val parameters = OAuth1RouterParameters("http://localhost:8082/callback", "",
                consumerKey, consumerSecret, GarminApi())
        val authRouter = OAuth1Router(vertx, parameters, userDataService)

        val garminDataService = ThirdPartyPushServiceImpl(kafkaProducerService)

        return GarminVerticle(garminDataService, authRouter)
    }

    private fun initFitbitVerticle(userDataService: UserDataService, kafkaProducerService: KafkaProducerService): FitbitVerticle {
        // Put Client ID/Secret in config
        val clientId = "abc"
        val clientSecret = "abc"
        val httpService = HttpServiceImpl(HttpOAuth2ConnectorClient(WebClient.create(vertx)))
        val activityUrl = FitbitRestUrl("/1/user/[ownerId]/activities/date/[date].json")
        val sleepUrl = FitbitRestUrl("/1.2/user/[ownerId]/sleep/date/[date].json")
        val heartRateUrl = FitbitRestUrl("/1/user/[ownerId]/activities/heart/date/[date]/1d.json")
        val profileUrl = FitbitRestUrl("/1/user/[ownerId]/profile.json")
        val endpointMap = mapOf(
                Pair("activities", listOf(RestEndpoint(activityUrl, FitbitActivitiesSummary.serializer()))),
                Pair("sleep", listOf(RestEndpoint(sleepUrl, FitbitSleepLogSummary.serializer()))),
                Pair("heartrate", listOf(RestEndpoint(heartRateUrl, FitbitHeartRateSummary.serializer()))),
                Pair("profile", listOf(RestEndpoint(profileUrl, FitbitProfile.serializer())))
        )
        val refreshParameters = OAuth2RefreshParameters(host = "api.fitbit.com",
                refreshPath = "/oauth2/token",
                clientId = clientId,
                clientSecret = clientSecret
        )

        val tokenRefreshService = OAuth2TokenRefreshServiceImpl(WebClient.create(vertx), refreshParameters, userDataService)
        val notificationService = ThirdPartyNotificationServiceImpl(httpService, endpointMap, userDataService, kafkaProducerService, tokenRefreshService)

        val oauth2 = OAuth2Auth.create(vertx, oAuth2ClientOptionsOf(
                authorizationPath = "https://www.fitbit.com/oauth2/authorize",
                flow = OAuth2FlowType.AUTH_CODE,
                clientID = clientId,
                clientSecret = clientSecret,
                tokenPath = "https://api.fitbit.com/oauth2/token"))
        val parameters = OAuth2RouterParameters("http://localhost:8080/login", "", "activity nutrition heartrate profile settings sleep weight")
        val authRouter = FitbitOAuth2Router(vertx, oauth2, parameters, userDataService)

        return FitbitVerticle(notificationService, authRouter)
    }
}
