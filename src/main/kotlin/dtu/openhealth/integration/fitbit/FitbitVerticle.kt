package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.service.ThirdPartyNotificationService
import dtu.openhealth.integration.shared.verticle.BaseNotificationEndpoint
import dtu.openhealth.integration.shared.web.auth.AuthorizationRouter
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.BodyHandler


class FitbitVerticle(notificationService: ThirdPartyNotificationService,
                     private val authRouter: AuthorizationRouter,
                     private val port: Int = 8180
) : BaseNotificationEndpoint(notificationService) {

    private fun handleNotification(routingContext : RoutingContext) {
        val jsonBody = routingContext.bodyAsJsonArray
        val notificationList = jsonBody.list
                .map { ThirdPartyNotification(it as Map<String, String>, "collectionType", "ownerId") }

        handleNotificationList(notificationList)
        routingContext.response().setStatusCode(204).end()
    }

    override fun start() {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/fitbit/notification").handler { handleNotification(it) }

        router.mountSubRouter("/", authRouter.getRouter())

        vertx.createHttpServer().requestHandler(router).listen(port)
    }
}