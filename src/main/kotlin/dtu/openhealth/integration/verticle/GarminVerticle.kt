package dtu.openhealth.integration.verticle

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class GarminVerticle : AbstractVerticle() {

    private fun handleBodyConsumptionSummary(routingContext : RoutingContext) {
        val jsonArray = routingContext.bodyAsJson.getJsonArray("body")
        println(jsonArray)
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