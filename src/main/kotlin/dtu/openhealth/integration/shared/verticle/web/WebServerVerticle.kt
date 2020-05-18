package dtu.openhealth.integration.shared.verticle.web

import dtu.openhealth.integration.fitbit.auth.FitbitOAuth2Router
import dtu.openhealth.integration.fitbit.FitbitRouter
import dtu.openhealth.integration.fitbit.data.activities.FitbitActivitiesSummary
import dtu.openhealth.integration.fitbit.data.heartrate.FitbitHeartRateSummary
import dtu.openhealth.integration.fitbit.data.profile.FitbitProfile
import dtu.openhealth.integration.fitbit.data.sleep.FitbitSleepLogSummary
import dtu.openhealth.integration.garmin.GarminRouter
import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.http.HttpServiceImpl
import dtu.openhealth.integration.shared.service.tokenrefresh.AOAuth2TokenRefreshServiceImpl
import dtu.openhealth.integration.shared.service.push.ThirdPartyPushServiceImpl
import dtu.openhealth.integration.shared.util.PropertiesLoader
import dtu.openhealth.integration.fitbit.FitbitRestUrl
import dtu.openhealth.integration.fitbit.service.tokenrefresh.FitbitTokenRefreshServiceImpl
import dtu.openhealth.integration.garmin.auth.GarminOAuth1Router
import dtu.openhealth.integration.shared.service.notification.ThirdPartyNotificationServiceImpl
import dtu.openhealth.integration.shared.web.http.HttpOAuth2ConnectorClient
import dtu.openhealth.integration.shared.web.auth.GarminApi
import dtu.openhealth.integration.shared.web.parameters.OAuth1RouterParameters
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.PemKeyCertOptions
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.reactivex.ext.web.Router
import io.vertx.kotlin.ext.auth.oauth2.oAuth2ClientOptionsOf
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import io.vertx.reactivex.ext.web.client.WebClient

class WebServerVerticle(
        private val userTokenDataService: IUserTokenDataService,
        private val kafkaProducerService: IKafkaProducerService
): AbstractVerticle() {

    private val configuration = PropertiesLoader.loadProperties()
    private val logger = LoggerFactory.getLogger(WebServerVerticle::class.java)

    override fun start()
    {
        val garminRouter = initGarminRouter(vertx, kafkaProducerService)
        val fitbitRouter = initFitbitRouter(vertx, kafkaProducerService)

        val mainRouter = Router.router(vertx)
        mainRouter.mountSubRouter("/garmin", garminRouter.getRouter())
        mainRouter.mountSubRouter("/fitbit", fitbitRouter.getRouter())

        val port = configuration.getProperty("webserver.port").toInt()
        val httpServerOptions = HttpServerOptions()
                .setPort(port)
                .setSsl(true)
                .setPemKeyCertOptions(PemKeyCertOptions()
                        .addCertPath(configuration.getProperty("ssl.certificate.chain.file"))
                        .addKeyPath(configuration.getProperty("ssl.certificate.key.file")))

        vertx.createHttpServer(httpServerOptions)
                .requestHandler(mainRouter)
                .listen { ar ->
                    if (ar.succeeded()) {
                        logger.info("Web server verticle successfuly started on port: ${ar.result().actualPort()}")
                    } else {
                        logger.error(ar.cause())
                    }
                }

    }

    private fun initGarminRouter(vertx: Vertx, kafkaProducerService: IKafkaProducerService): GarminRouter
    {
        val consumerKey = configuration.getProperty("garmin.consumer.key")
        val consumerSecret = configuration.getProperty("garmin.consumer.secret")
        val parameters = OAuth1RouterParameters(configuration.getProperty("garmin.callback.url"), "",
                consumerKey, consumerSecret, GarminApi())
        val authRouter = GarminOAuth1Router(vertx, parameters, userTokenDataService)
        val garminDataService = ThirdPartyPushServiceImpl(kafkaProducerService)

        return GarminRouter(vertx, garminDataService, authRouter)
    }

    private fun initFitbitRouter(vertx: Vertx, kafkaProducerService: IKafkaProducerService): FitbitRouter
    {
        // Configuration parameters
        val clientId = configuration.getProperty("fitbit.client.id")
        val clientSecret = configuration.getProperty("fitbit.client.secret")
        val verificationCode = configuration.getProperty("fitbit.verify.code")
        val fitbitApiPort = configuration.getProperty("fitbit.api.port").toInt()

        // Initialization
        val httpService = HttpServiceImpl(HttpOAuth2ConnectorClient(WebClient.create(vertx), fitbitApiPort))
        val endpointMap = fitbitEndpointMap()
        val refreshParameters = fitbitRefreshParameters(clientId, clientSecret, fitbitApiPort)
        val tokenRefreshService = FitbitTokenRefreshServiceImpl(WebClient.create(vertx), refreshParameters, userTokenDataService)
        val notificationService = ThirdPartyNotificationServiceImpl(httpService, endpointMap, userTokenDataService,
                kafkaProducerService, tokenRefreshService)

        // Auth router
        val oauth2Options = fitbitOAuthClientOptions(clientId, clientSecret)
        val oauth2 = OAuth2Auth.create(vertx, oauth2Options)
        val parameters = OAuth2RouterParameters(
                configuration.getProperty("fitbit.oauth2.redirect.uri"),
                configuration.getProperty("fitbit.oauth2.return.uri"),
                configuration.getProperty("fitbit.oauth2.scope")
        )
        val authRouter = FitbitOAuth2Router(vertx, oauth2, parameters, userTokenDataService)

        return FitbitRouter(vertx, notificationService, authRouter, verificationCode)
    }

    private fun fitbitEndpointMap(): Map<String,List<RestEndpoint>> {
        val activityUrl = FitbitRestUrl("/1/user/[ownerId]/activities/date/[date].json")
        val sleepUrl = FitbitRestUrl("/1.2/user/[ownerId]/sleep/date/[date].json")
        val heartRateUrl = FitbitRestUrl("/1/user/[ownerId]/activities/heart/date/[date]/1d.json")
        val profileUrl = FitbitRestUrl("/1/user/[ownerId]/profile.json")

        return mapOf(
                Pair("activities", listOf(RestEndpoint(activityUrl, FitbitActivitiesSummary.serializer()))),
                Pair("sleep", listOf(RestEndpoint(sleepUrl, FitbitSleepLogSummary.serializer()))),
                Pair("heartrate", listOf(RestEndpoint(heartRateUrl, FitbitHeartRateSummary.serializer()))),
                Pair("profile", listOf(RestEndpoint(profileUrl, FitbitProfile.serializer())))
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

    private fun fitbitOAuthClientOptions(clientId: String, clientSecret: String): OAuth2ClientOptions
    {
        return oAuth2ClientOptionsOf(
                authorizationPath = "https://www.fitbit.com/oauth2/authorize",
                flow = OAuth2FlowType.AUTH_CODE,
                clientID = clientId,
                clientSecret = clientSecret,
                tokenPath = "https://api.fitbit.com/oauth2/token"
        )
    }
}
