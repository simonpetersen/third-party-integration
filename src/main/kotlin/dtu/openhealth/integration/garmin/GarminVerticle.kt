package dtu.openhealth.integration.garmin

import dtu.openhealth.integration.garmin.garmin.*
import dtu.openhealth.integration.kafka.producer.KafkaProducerService
import dtu.openhealth.integration.shared.service.GarminDataService
import dtu.openhealth.integration.shared.service.impl.GarminDataServiceImpl
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class GarminVerticle(kafkaProducerService: KafkaProducerService) : AbstractVerticle() {

    private val LOGGER = LoggerFactory.getLogger(GarminVerticle::class.java)
    private val garminDataService: GarminDataService = GarminDataServiceImpl(kafkaProducerService)

    override fun start() {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/api/garmin/activities").handler { handleActivitySummary(it) }
        router.post("/api/garmin/body").handler { handleBodyConsumptionSummary(it) }
        router.post("/api/garmin/dailies").handler { handleDailySummary(it) }
        router.post("/api/garmin/epochs").handler { handleEpochSummary(it) }
        router.post("/api/garmin/respirations").handler { handleRespirationSummary(it) }
        router.post("/api/garmin/sleep").handler { handleSleepSummary(it) }
        router.post("/api/garmin/thirdparty").handler { handleThirdPartySummary(it) }
        router.post("/api/garmin/pulse").handler { handlePulseSummary(it) }

        vertx.createHttpServer().requestHandler(router).listen(8082)
    }

    private fun handleActivitySummary(routingContext : RoutingContext) {
        LOGGER.info("Posting activity summary data for Garmin")
        val activitySummaries: JsonArray = routingContext.bodyAsJson.getJsonArray("activities")
        activitySummaries.stream().forEach {
            data -> if(data is JsonObject) {
                try{
                    data.mapTo(ActivitySummaryGarmin::class.java).also {
                        LOGGER.info("Saving data to Garmin: $it")
                        garminDataService.saveDataToOMH(it)
                    }
                }catch (e: Exception){
                    LOGGER.error(e.message)
                }
            }
        }
        routingContext.response().end()
    }

    private fun handleBodyConsumptionSummary(routingContext : RoutingContext) {
        LOGGER.info("Posting body consumption data for Garmin")
        val bodyCompositionSummaries: JsonArray = routingContext.bodyAsJson.getJsonArray("body")
        bodyCompositionSummaries.stream().forEach {
            data -> if(data is JsonObject) {
                try{
                    data.mapTo(BodyCompositionSummaryGarmin::class.java).also {
                        LOGGER.info("Saving data to Garmin: $it")
                        garminDataService.saveDataToOMH(it)
                    }
                }catch (e: Exception){
                    LOGGER.error(e.message)
                }
            }
        }
        routingContext.response().end()
    }

    private fun handleDailySummary(routingContext : RoutingContext) {
        LOGGER.info("Posting daily summary data for Garmin")
        val dailySummaries = routingContext.bodyAsJson.getJsonArray("dailies")
        dailySummaries.stream().forEach {
            data -> if(data is JsonObject) {
                try{
                    data.mapTo(DailySummaryGarmin::class.java).also {
                        LOGGER.info("Saving data to Garmin: $it")
                        garminDataService.saveDataToOMH(it)
                    }
                }catch (e: Exception){
                    LOGGER.error(e.message)
                }
            }
        }
        routingContext.response().end()
    }

    private fun handleEpochSummary(routingContext : RoutingContext) {
        LOGGER.info("Posting epoch summary data for Garmin")
        val epochsSummaries: JsonArray = routingContext.bodyAsJson.getJsonArray("epochs")
        epochsSummaries.stream().forEach {
            data -> if(data is JsonObject) {
                try{
                    data.mapTo(EpochSummaryGarmin::class.java).also {
                        LOGGER.info("Saving data to Garmin: $it")
                        garminDataService.saveDataToOMH(it)
                    }
                }catch (e: Exception){
                    LOGGER.error(e.message)
                }
            }
        }
        routingContext.response().end()
    }

    private fun handleRespirationSummary(routingContext : RoutingContext) {
        LOGGER.info("Posting respiration summary data for Garmin")
        val respirationSummary = routingContext.bodyAsJson.getJsonArray("respirations")
        respirationSummary.stream().forEach {
            data -> if(data is JsonObject) {
                try{
                    data.mapTo(RespirationSummaryGarmin::class.java).also {
                        LOGGER.info("Saving data to Garmin: $it")
                        garminDataService.saveDataToOMH(it)
                    }
                }catch (e: Exception){
                    LOGGER.error(e.message)
                }
            }
        }
        routingContext.response().end()
    }

    private fun handleSleepSummary(routingContext : RoutingContext) {
        LOGGER.info("Posting sleep summary data for Garmin")
        val sleepSummary = routingContext.bodyAsJson.getJsonArray("sleeps")
        sleepSummary.stream().forEach {
            data -> if(data is JsonObject) {
                try{
                    data.mapTo(SleepSummaryGarmin::class.java).also {
                        LOGGER.info("Saving data to Garmin: $it")
                        garminDataService.saveDataToOMH(it)
                    }
                }catch (e: Exception){
                    LOGGER.error(e.message)
                }
            }
        }
        routingContext.response().end()
    }

    private fun handleThirdPartySummary(routingContext : RoutingContext) {
        LOGGER.info("Posting third party summary data for Garmin")
        val thirdPartySummaries = routingContext.bodyAsJson.getJsonArray("thirdparty")
        thirdPartySummaries.stream().forEach {
            data -> if(data is JsonObject) {
                try{
                    data.mapTo(ThirdPartyDailySummaryGarmin::class.java).also {
                        LOGGER.info("Saving data to Garmin: $it")
                        garminDataService.saveDataToOMH(it)
                    }
                }catch (e: Exception){
                    LOGGER.error(e.message)
                }
            }
        }
        routingContext.response().end()
    }


    private fun handlePulseSummary(routingContext : RoutingContext) {
        LOGGER.info("Posting pulse summary data for Garmin")
        val pulseSummaries = routingContext.bodyAsJson.getJsonArray("pulseOX")
        pulseSummaries.stream().forEach {
            data -> if(data is JsonObject) {
                try{
                    data.mapTo(PulseOXSummaryGarmin::class.java).also {
                        LOGGER.info("Saving data to Garmin: $it")
                        garminDataService.saveDataToOMH(it)
                    }
                }catch (e: Exception){
                    LOGGER.error(e.message)
                }
            }
        }
        routingContext.response().end()
    }
}
