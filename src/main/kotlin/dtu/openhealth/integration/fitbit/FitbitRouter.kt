package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.service.ThirdPartyNotificationService
import dtu.openhealth.integration.shared.util.PropertiesLoader
import dtu.openhealth.integration.shared.verticle.BaseNotificationEndpointRouter
import dtu.openhealth.integration.shared.web.auth.AuthorizationRouter
import dtu.openhealth.integration.shared.web.auth.OAuth2Router
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.BodyHandler


class FitbitRouter(private val vertx: Vertx,
                   notificationService: ThirdPartyNotificationService,
                   private val authRouter: AuthorizationRouter)
    : BaseNotificationEndpointRouter(vertx, notificationService) {

    private val configuration = PropertiesLoader.loadProperties()

    fun getRouter() : Router {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/notification").handler { handleNotification(it) }
        router.get("/notification/webhook").handler { handleVerification(it) }

        router.mountSubRouter("/", authRouter.getRouter())

        return router
    }

    private fun handleNotification(routingContext : RoutingContext) {
        val jsonBody = routingContext.bodyAsJsonArray
        val notificationList = jsonBody.list
                .map { ThirdPartyNotification(it as Map<String, String>, "collectionType", "ownerId") }

        handleNotificationList(notificationList)
        routingContext.response().setStatusCode(204).end()
    }

    private fun handleVerification(routingContext: RoutingContext) {
        val verificationCode = routingContext.request().getParam("verify")
        val correctVerificationCode = configuration.getProperty("fitbit.verify.code")

        if (verificationCode != correctVerificationCode) {
            routingContext
                    .response()
                    .setStatusCode(404)
                    .end()
        }

        routingContext.response().setStatusCode(204).end()
    }
}
