package dtu.openhealth.integration.shared.verticle

import dtu.openhealth.integration.shared.service.impl.*
import dtu.openhealth.integration.shared.util.PropertiesLoader
import io.vertx.reactivex.core.AbstractVerticle

class  MainVerticle : AbstractVerticle() {

    private val configuration = PropertiesLoader.loadProperties()

    override fun start() {
        val userDataService = UserDataServiceImpl(vertx.delegate)
        //val kafkaProducerService = KafkaProducerServiceImpl(vertx)

        // Deploy verticles
        //vertx.deployVerticle(initFitbitVerticle(userDataService, kafkaProducerService))
        //vertx.deployVerticle(initFitbitVerticle(userDataService))
        //vertx.deployVerticle(initGarminVerticle(userDataService, kafkaProducerService))
        vertx.deployVerticle(WebServerVerticle(userDataService))
        //vertx.deployVerticle(OmhConsumerVerticle(userDataService))
    }

    /*
    private fun initGarminVerticle(userDataService: UserDataService, kafkaProducerService: KafkaProducerService): GarminVerticle {
        val consumerKey = configuration.getProperty("garmin.consumer.key")
        val consumerSecret = configuration.getProperty("garmin.consumer.secret")
        val parameters = OAuth1RouterParameters(configuration.getProperty("garmin.callback.url"), "",
                consumerKey, consumerSecret, GarminApi())
        val authRouter = OAuth1Router(vertx, parameters, userDataService)

        val garminDataService = ThirdPartyPushServiceImpl(kafkaProducerService)

        return GarminVerticle(garminDataService, authRouter)
    }

    private fun initFitbitVerticle(userDataService: UserDataService, kafkaProducerService: KafkaProducerService? = null): FitbitVerticle {
        // Put Client ID/Secret in config
        val clientId = configuration.getProperty("fitbit.client.id")
        val clientSecret = configuration.getProperty("fitbit.client.secret")
        val httpService = HttpServiceImpl(HttpOAuth2ConnectorClient(WebClient.create(vertx)))
        val activityUrl = FitbitRestUrl("/1/user/[ownerId]/activities/date/[date].json")
        val sleepUrl = FitbitRestUrl("/1.2/user/[ownerId]/sleep/date/[date].json")
        val heartRateUrl = FitbitRestUrl("/1/user/[ownerId]/activities/heart/date/[date]/1d.json")
        val profileUrl = FitbitRestUrl("/1/user/[ownerId]/profile.json")
        val endpointMap = mapOf(
                Pair("activities", listOf(RestEndpoint(activityUrl, FitbitActivitiesSummary.serializer()),
                        RestEndpoint(sleepUrl, FitbitSleepLogSummary.serializer()),
                        RestEndpoint(heartRateUrl, FitbitHeartRateSummary.serializer()),
                        RestEndpoint(profileUrl, FitbitProfile.serializer()))
                )
                //Pair("sleep", listOf(RestEndpoint(sleepUrl, FitbitSleepLogSummary.serializer()))),
                //Pair("heartrate", listOf(RestEndpoint(heartRateUrl, FitbitHeartRateSummary.serializer()))),
                //Pair("profile", listOf(RestEndpoint(profileUrl, FitbitProfile.serializer())))
        )
        val refreshParameters = OAuth2RefreshParameters(host = "api.fitbit.com",
                refreshPath = "/oauth2/token",
                clientId = clientId,
                clientSecret = clientSecret
        )

        val tokenRefreshService = OAuth2TokenRefreshServiceImpl(WebClient.create(vertx), refreshParameters, userDataService)
        val notificationService = ThirdPartyNotificationServiceImpl(httpService, endpointMap, userDataService,
                kafkaProducerService, tokenRefreshService)

        val oauth2 = OAuth2Auth.create(vertx, oAuth2ClientOptionsOf(
                authorizationPath = "https://www.fitbit.com/oauth2/authorize",
                flow = OAuth2FlowType.AUTH_CODE,
                clientID = clientId,
                clientSecret = clientSecret,
                tokenPath = "https://api.fitbit.com/oauth2/token"))
        val parameters = OAuth2RouterParameters(configuration.getProperty("fitbit.oauth2.redirect.uri"),
                "", "activity nutrition heartrate profile settings sleep weight")
        val authRouter = FitbitOAuth2Router(vertx, oauth2, parameters, userDataService)

        return FitbitVerticle(notificationService, authRouter)
    }

     */
}
