package dtu.openhealth.integration.garmin

import dtu.openhealth.integration.garmin.data.*
import dtu.openhealth.integration.shared.util.PropertiesLoader
import dtu.openhealth.integration.shared.service.ThirdPartyPushService
import dtu.openhealth.integration.shared.verticle.BasePushEndpointRouter
import dtu.openhealth.integration.shared.web.auth.AuthorizationRouter
import io.vertx.core.json.JsonArray
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.BodyHandler

class GarminVerticle(private val vertx: Vertx,
                     pushService: ThirdPartyPushService,
                     private val authRouter: AuthorizationRouter) : BasePushEndpointRouter(pushService) {

    private val logger = LoggerFactory.getLogger(GarminVerticle::class.java)
    private val configuration =  PropertiesLoader.loadProperties()

    fun getRouter() : Router {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/activities").handler { handleActivitySummary(it) }
        router.post("/body").handler { handleBodyConsumptionSummary(it) }
        router.post("/dailies").handler { handleDailySummary(it) }
        router.post("/epochs").handler { handleEpochSummary(it) }
        router.post("/respirations").handler { handleRespirationSummary(it) }
        router.post("/sleep").handler { handleSleepSummary(it) }
        router.post("/thirdparty").handler { handleThirdPartySummary(it) }
        router.post("/pulse").handler { handlePulseSummary(it) }

        router.mountSubRouter("/", authRouter.getRouter())

        /*
        vertx.createHttpServer().requestHandler(router).listen(
                configuration.getProperty("garmin.verticle.port").toInt())

         */

        return router
    }

    private fun handleActivitySummary(routingContext : RoutingContext) {
        logger.info("Posting activity summary data for Garmin")
        val activitySummaries: JsonArray = routingContext.bodyAsJson.getJsonArray("activities")
        convertArrayAndSaveData(activitySummaries, ActivitySummaryGarmin.serializer())
        routingContext.response().end()
    }

    private fun handleBodyConsumptionSummary(routingContext : RoutingContext) {
        logger.info("Posting body consumption data for Garmin")
        val bodyCompositionSummaries: JsonArray = routingContext.bodyAsJson.getJsonArray("body")
        convertArrayAndSaveData(bodyCompositionSummaries, BodyCompositionSummaryGarmin.serializer())
        routingContext.response().end()
    }

    private fun handleDailySummary(routingContext : RoutingContext) {
        logger.info("Posting daily summary data for Garmin")
        val dailySummaries = routingContext.bodyAsJson.getJsonArray("dailies")
        convertArrayAndSaveData(dailySummaries, DailySummaryGarmin.serializer())
        routingContext.response().end()
    }

    private fun handleEpochSummary(routingContext : RoutingContext) {
        logger.info("Posting epoch summary data for Garmin")
        val epochsSummaries: JsonArray = routingContext.bodyAsJson.getJsonArray("epochs")
        convertArrayAndSaveData(epochsSummaries, EpochSummaryGarmin.serializer())
        routingContext.response().end()
    }

    private fun handleRespirationSummary(routingContext : RoutingContext) {
        logger.info("Posting respiration summary data for Garmin")
        val respirationSummary = routingContext.bodyAsJson.getJsonArray("respirations")
        convertArrayAndSaveData(respirationSummary, RespirationSummaryGarmin.serializer())
        routingContext.response().end()
    }

    private fun handleSleepSummary(routingContext : RoutingContext) {
        logger.info("Posting sleep summary data for Garmin")
        val sleepSummary = routingContext.bodyAsJson.getJsonArray("sleeps")
        convertArrayAndSaveData(sleepSummary, SleepSummaryGarmin.serializer())
        routingContext.response().end()
    }

    private fun handleThirdPartySummary(routingContext : RoutingContext) {
        logger.info("Posting third party summary data for Garmin")
        val thirdPartySummaries = routingContext.bodyAsJson.getJsonArray("thirdparty")
        convertArrayAndSaveData(thirdPartySummaries, ThirdPartyDailySummaryGarmin.serializer())
        routingContext.response().end()
    }


    private fun handlePulseSummary(routingContext : RoutingContext) {
        logger.info("Posting pulse summary data for Garmin")
        val pulseSummaries = routingContext.bodyAsJson.getJsonArray("pulseOX")
        convertArrayAndSaveData(pulseSummaries, PulseOXSummaryGarmin.serializer())
        routingContext.response().end()
    }
}
