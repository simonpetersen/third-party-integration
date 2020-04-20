package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.service.ThirdPartyNotificationService
import dtu.openhealth.integration.shared.service.impl.VertxUserServiceImpl
import dtu.openhealth.integration.shared.web.auth.*
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.kotlin.ext.auth.oauth2.oAuth2ClientOptionsOf
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.BodyHandler
import kotlinx.coroutines.*

class FitbitVerticle(private val notificationService: ThirdPartyNotificationService) : AbstractVerticle() {

    // Put Client ID/Secret in config or constant file
    private val clientId = "123"
    private val clientSecret = "123"

    private fun handleNotification(routingContext : RoutingContext) = runBlocking {
        val jsonBody = routingContext.bodyAsJsonArray
        val notificationList = jsonBody.list
                .map { ThirdPartyNotification(it as Map<String, String>, "collectionType", "ownerId") }

        launch { notificationService.getUpdatedData(notificationList) } // Retrieve data in coroutine and reply immediately

        routingContext.response().setStatusCode(204).end()
    }

    override fun start() {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/fitbit/notification").handler { handleNotification(it) }

        val oauth2 = OAuth2Auth.create(vertx, oAuth2ClientOptionsOf(
                authorizationPath = "https://www.fitbit.com/oauth2/authorize",
                flow = OAuth2FlowType.AUTH_CODE,
                clientID = clientId,
                clientSecret = clientSecret,
                tokenPath = "https://api.fitbit.com/oauth2/token"))
        val parameters = OAuth2RouterParameters("http://localhost:8080/login", "", "activity nutrition heartrate profile settings sleep social weight")
        val authRouter = OAuth2Router(vertx, oauth2, parameters, VertxUserServiceImpl(vertx.delegate)).getRouter()
        router.mountSubRouter("/", authRouter)

        /*
        val parameters = OAuth1Parameters("http://localhost:8080/callback",
                "",
                "connectapi.garmin.com",
                "df797a09-77d9-4a86-9824-3f0d7d234068",
                "bM4bF9mcfutToV17YEDIjQSVECQnIltaUM5",
                GarminApi.instance())
        val authRouter = OAuth1Router(vertx, parameters).getRouter()
        router.mountSubRouter("/", authRouter)

         */

        vertx.createHttpServer().requestHandler(router).listen(8080)
    }
}