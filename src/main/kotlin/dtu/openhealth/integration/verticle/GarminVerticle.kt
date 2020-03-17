package dtu.openhealth.integration.verticle

import dtu.openhealth.integration.data.garmin.BodyCompositionSummaryGarmin
import dtu.openhealth.integration.service.GarminDataService
import dtu.openhealth.integration.service.impl.GarminDataServiceImpl
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class GarminVerticle: AbstractVerticle() {

    private val garminDataService: GarminDataService = GarminDataServiceImpl()

    private fun handleBodyConsumptionSummary(routingContext : RoutingContext) {
        val bodyCompositionSummaries: JsonArray = routingContext.bodyAsJson.getJsonArray("body")
        bodyCompositionSummaries.stream().forEach {
            data -> if(data is JsonObject) {
                try{
                    data.mapTo(BodyCompositionSummaryGarmin::class.java).also {
                        garminDataService.saveBodyCompositionSummaryData(it)
                    }
                }catch (e: Exception){
                    println(e.message)
                }
            }
        }
        routingContext.response().end()
    }

    private fun handleDailySummary(routingContext : RoutingContext) {
        val jsonArray = routingContext.bodyAsJson.getJsonArray("dailies")
        println(jsonArray)
        routingContext.response().end()
    }

    override fun start() {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/garmin/body").handler { handleBodyConsumptionSummary(it) }
        router.post("/garmin/daily").handler { handleDailySummary(it) }

        vertx.createHttpServer().requestHandler(router).listen(8082)
    }
}
