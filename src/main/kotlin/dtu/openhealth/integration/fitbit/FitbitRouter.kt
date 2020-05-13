package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.service.ThirdPartyNotificationService
import dtu.openhealth.integration.shared.util.PropertiesLoader
import dtu.openhealth.integration.shared.verticle.BaseNotificationEndpointRouter
import dtu.openhealth.integration.shared.web.auth.AuthorizationRouter
import dtu.openhealth.integration.shared.web.auth.OAuth2Router
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.BodyHandler


class FitbitRouter(
        private val vertx: Vertx,
        notificationService: ThirdPartyNotificationService,
        private val authRouter: AuthorizationRouter,
        private val verificationCode: String
): BaseNotificationEndpointRouter(vertx, notificationService) {

    private val logger = LoggerFactory.getLogger(FitbitRouter::class.java)

    fun getRouter() : Router
    {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/notification").handler { handleNotification(it) }
        router.get("/notification/fitbit/webhook").handler {
            logger.info("/notification/fitbit/webhook called for verification")
            handleVerification(it) }
        router.get("/notification/webhook").handler {
            logger.info("/notification/webhook called for verification")
            handleVerification(it) }
        router.get("/webhook").handler {
            logger.info("/webhook called for verification")
            handleVerification(it) }

        router.mountSubRouter("/", authRouter.getRouter())

        return router
    }

    private fun handleNotification(routingContext : RoutingContext)
    {
        logger.info("FitbitRouter handleNotification called. Body: ${routingContext.bodyAsString}")
        val jsonBody = routingContext.bodyAsJsonArray
        val notificationList = jsonBody.list
                .map { ThirdPartyNotification(it as Map<String, String>, "collectionType", "ownerId") }

        handleNotificationList(notificationList)
        routingContext.response().setStatusCode(204).end()
    }

    private fun handleVerification(routingContext: RoutingContext)
    {
        val verifyCode = routingContext.request().getParam("verify")

        if (verifyCode != verificationCode) {
            routingContext
                    .response()
                    .setStatusCode(404)
                    .end()
        }

        routingContext.response().setStatusCode(204).end()
    }
}
