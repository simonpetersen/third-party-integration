package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.fitbit.data.activities.FitbitActivitiesSummary
import dtu.openhealth.integration.fitbit.data.heartrate.FitbitHeartRateSummary
import dtu.openhealth.integration.fitbit.data.profile.FitbitProfile
import dtu.openhealth.integration.fitbit.data.sleep.FitbitSleepLogSummary
import dtu.openhealth.integration.fitbit.service.pull.FitbitPullService
import dtu.openhealth.integration.fitbit.service.token.refresh.FitbitTokenRefreshServiceImpl
import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.http.HttpServiceImpl
import dtu.openhealth.integration.shared.util.PropertiesLoader
import dtu.openhealth.integration.shared.verticle.pull.BasePullVerticle
import dtu.openhealth.integration.shared.web.http.HttpOAuth2ConnectorClient
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.ext.web.client.WebClient

class FitbitPullVerticle(
        private val userTokenDataService: IUserTokenDataService,
        private val kafkaProducerService: IKafkaProducerService
): BasePullVerticle() {

    private val configuration = PropertiesLoader.loadProperties()
    private val logger = LoggerFactory.getLogger(FitbitPullVerticle::class.java)

    override fun start()
    {
        val fitbitPullEnabled = configuration.getProperty("fitbit.pull.enabled")?.toBoolean()
        if (fitbitPullEnabled == null || !fitbitPullEnabled) {
            logger.info("Fitbit pull service disabled in configuration")
            return
        }

        val clientId = configuration.getProperty("fitbit.client.id")
        val clientSecret = configuration.getProperty("fitbit.client.secret")
        val fitbitApiPort = configuration.getProperty("fitbit.api.port").toInt()
        val fitbitIntervalMinutes = configuration.getProperty("fitbit.pull.interval.minutes").toInt()
        val httpService = HttpServiceImpl(HttpOAuth2ConnectorClient(WebClient.create(vertx), fitbitApiPort))
        val endpointList = getFitbitEndpoints()

        val refreshParameters = fitbitRefreshParameters(clientId, clientSecret, fitbitApiPort)
        val tokenRefreshService = FitbitTokenRefreshServiceImpl(WebClient.create(vertx), refreshParameters, userTokenDataService)

        val pullService = FitbitPullService(httpService, endpointList, kafkaProducerService, tokenRefreshService, userTokenDataService)

        startTimer(pullService, fitbitIntervalMinutes)
    }

    private fun getFitbitEndpoints(): List<RestEndpoint>
    {
        val activityUrl = FitbitRestUrl("/1/user/[userId]/activities/date/[date].json")
        val sleepUrl = FitbitRestUrl("/1.2/user/[userId]/sleep/date/[date].json")
        val heartRateUrl = FitbitRestUrl("/1/user/[userId]/activities/heart/date/[date]/1d.json")
        val profileUrl = FitbitRestUrl("/1/user/[ownerId]/profile.json")

        return listOf(
                RestEndpoint(activityUrl, FitbitActivitiesSummary.serializer()),
                RestEndpoint(sleepUrl, FitbitSleepLogSummary.serializer()),
                RestEndpoint(heartRateUrl, FitbitHeartRateSummary.serializer()),
                RestEndpoint(profileUrl, FitbitProfile.serializer())
        )
    }

    private fun fitbitRefreshParameters(clientId: String, clientSecret: String, port: Int): OAuth2RefreshParameters
    {
        return OAuth2RefreshParameters(
                host = "api.fitbit.com",
                refreshPath = "/oauth2/token",
                clientId = clientId,
                clientSecret = clientSecret,
                port = port
        )
    }
}