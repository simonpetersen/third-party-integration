package dtu.openhealth.integration.verticle

import dtu.openhealth.integration.model.ThirdPartyNotification
import dtu.openhealth.integration.service.RestConnectorService
import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class FitbitVerticle : AbstractVerticle() {

    private val restConnectorService = RestConnectorService()

    private fun handleNotification(routingContext : RoutingContext) {
        val jsonBody = routingContext.bodyAsJsonArray
        for (notification in jsonBody.list) {
            val fitbitNotification = ThirdPartyNotification(notification as Map<String, String>, "collectionType", "ownerId")
            println(fitbitNotification)
            //restConnectorService.retrieveDataForUser(fitbitNotification)
        }

        val response = routingContext.response()
        response.statusCode = 204
        response.end()
    }

    override fun start() {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/fitbit/notification").handler { handleNotification(it) }

        vertx.createHttpServer().requestHandler(router).listen(8080)
    }
}