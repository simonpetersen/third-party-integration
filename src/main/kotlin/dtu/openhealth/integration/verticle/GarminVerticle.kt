package dtu.openhealth.integration.verticle

import dtu.openhealth.integration.data.garmin.ActivitySummaryGarmin
import dtu.openhealth.integration.data.garmin.BodyCompositionSummaryGarmin
import dtu.openhealth.integration.data.garmin.DailySummaryGarmin
import dtu.openhealth.integration.service.GarminDataService
import dtu.openhealth.integration.service.impl.GarminDataServiceImpl
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class GarminVerticle: AbstractVerticle() {

    private val LOGGER = LoggerFactory.getLogger(GarminVerticle::class.java)
    private val garminDataService: GarminDataService = GarminDataServiceImpl()

    private fun handleActivitySummary(routingContext : RoutingContext) {
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
    }

    private fun handleBodyConsumptionSummary(routingContext : RoutingContext) {
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

    override fun start() {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/api/garmin/activities").handler { handleActivitySummary(it) }
        router.post("/api/garmin/body").handler { handleBodyConsumptionSummary(it) }

        vertx.createHttpServer().requestHandler(router).listen(8082)
    }
}
