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
import dtu.openhealth.integration.shared.service.push.ThirdPartyPushServiceImpl
import dtu.openhealth.integration.shared.util.PropertiesLoader
import dtu.openhealth.integration.fitbit.FitbitRestUrl
import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.fitbit.service.token.refresh.FitbitTokenRefreshServiceImpl
import dtu.openhealth.integration.fitbit.service.token.revoke.FitbitTokenRevokeService
import dtu.openhealth.integration.garmin.auth.GarminOAuth1Router
import dtu.openhealth.integration.shared.service.notification.ThirdPartyNotificationServiceImpl
import dtu.openhealth.integration.shared.web.http.HttpOAuth2ConnectorClient
import dtu.openhealth.integration.garmin.auth.GarminApi
import dtu.openhealth.integration.garmin.data.GarminConstants
import dtu.openhealth.integration.shared.service.token.revoke.ITokenRevokeService
import dtu.openhealth.integration.shared.service.token.revoke.OAuth1TokenRevokeService
import dtu.openhealth.integration.shared.service.token.revoke.data.OAuth1RevokeParameters
import dtu.openhealth.integration.shared.service.token.revoke.data.OAuth2RevokeParameters
import dtu.openhealth.integration.shared.web.parameters.OAuth1RouterParameters
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import dtu.openhealth.integration.shared.web.router.RevokeTokensRouter
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.kotlin.core.http.httpServerOptionsOf
import io.vertx.kotlin.core.net.pemKeyCertOptionsOf
import io.vertx.reactivex.ext.web.Router
import io.vertx.kotlin.ext.auth.oauth2.oAuth2ClientOptionsOf
import io.vertx.reactivex.core.AbstractVerticle
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
        val garminRouter = initGarminRouter(kafkaProducerService)
        val fitbitRouter = initFitbitRouter(kafkaProducerService)
        val revokeRouter = initRevokeTokensRouter(userTokenDataService)

        val mainRouter = Router.router(vertx)
        mainRouter.mountSubRouter("/garmin", garminRouter.getRouter())
        mainRouter.mountSubRouter("/fitbit", fitbitRouter.getRouter())
        mainRouter.mountSubRouter("/", revokeRouter.getRouter())

        val port = configuration.getProperty("webserver.port").toInt()
        val httpServerOptions = httpServerOptionsOf(
                port = port,
                ssl = true,
                pemKeyCertOptions = pemKeyCertOptionsOf(
                        certPath = configuration.getProperty("ssl.certificate.chain.file"),
                        keyPath = configuration.getProperty("ssl.certificate.key.file")
                )
        )

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

    private fun initGarminRouter(kafkaProducerService: IKafkaProducerService): GarminRouter
    {
        val consumerKey = configuration.getProperty("garmin.consumer.key")
        val consumerSecret = configuration.getProperty("garmin.consumer.secret")
        val callbackUrl = configuration.getProperty("garmin.callback.url")
        val resultUrl = configuration.getProperty("garmin.result.url")
        val parameters = OAuth1RouterParameters(
                callbackUrl,
                resultUrl,
                consumerKey,
                consumerSecret,
                GarminApi()
        )
        val authRouter = GarminOAuth1Router(vertx, parameters, userTokenDataService)
        val garminDataService = ThirdPartyPushServiceImpl(kafkaProducerService)

        return GarminRouter(vertx, garminDataService, authRouter)
    }

    private fun initFitbitRouter(kafkaProducerService: IKafkaProducerService): FitbitRouter
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
                configuration.getProperty("fitbit.oauth2.scope"),
                FitbitConstants.Host,
                configuration.getProperty("fitbit.oauth2.subscription.uri"),
                fitbitApiPort
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

    private fun initRevokeTokensRouter(userTokenDataService: IUserTokenDataService): RevokeTokensRouter
    {
        val acceptedStatusCodes = listOf(200,204)
        val revokeServiceMap = mapOf(
                garminRevokeService(),
                fitbitRevokeService()
        )

        return RevokeTokensRouter(vertx, userTokenDataService, acceptedStatusCodes, revokeServiceMap)
    }

    private fun garminRevokeService(): Pair<String, ITokenRevokeService>
    {
        val webClient = WebClient.create(vertx)
        val garminHost = configuration.getProperty("garmin.api.host")
        val garminPort = configuration.getProperty("garmin.api.port").toInt()
        val revokeUrl = configuration.getProperty("garmin.oauth.revoke.url")
        val consumerKey = configuration.getProperty("garmin.consumer.key")
        val consumerSecret = configuration.getProperty("garmin.consumer.secret")
        val parameters = OAuth1RevokeParameters(garminHost,revokeUrl,consumerKey, consumerSecret, garminPort, GarminApi())
        val tokenRevokeService = OAuth1TokenRevokeService(webClient, parameters)

        return Pair(GarminConstants.Garmin, tokenRevokeService)
    }

    private fun fitbitRevokeService(): Pair<String, ITokenRevokeService>
    {
        val webClient = WebClient.create(vertx)

        // Parameters
        val clientId = configuration.getProperty("fitbit.client.id")
        val clientSecret = configuration.getProperty("fitbit.client.secret")
        val fitbitApiPort = configuration.getProperty("fitbit.api.port").toInt()
        val subscriptionUrl = configuration.getProperty("fitbit.oauth2.subscription.uri")
        val revokeUrl = configuration.getProperty("fitbit.oauth2.revoke.uri")
        val parameters = OAuth2RevokeParameters(FitbitConstants.Host, revokeUrl, subscriptionUrl, clientId, clientSecret, fitbitApiPort)

        // TokenRefreshService
        val refreshParameters = fitbitRefreshParameters(clientId, clientSecret, fitbitApiPort)
        val tokenRefreshService = FitbitTokenRefreshServiceImpl(webClient, refreshParameters, userTokenDataService)

        val tokenRevokeService = FitbitTokenRevokeService(webClient, parameters, tokenRefreshService)
        return Pair(FitbitConstants.Fitbit, tokenRevokeService)
    }
}
