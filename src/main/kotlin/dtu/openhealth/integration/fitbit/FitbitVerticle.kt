package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.service.ThirdPartyNotificationService
import dtu.openhealth.integration.shared.service.impl.VertxUserServiceImpl
import dtu.openhealth.integration.shared.web.auth.OAuth2Router
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.ext.auth.oauth2.oAuth2ClientOptionsOf
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.BodyHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FitbitVerticle(private val notificationService: ThirdPartyNotificationService) : AbstractVerticle() {

    // Put Client ID/Secret in config or constant file
    private val clientId = "123"
    private val clientSecret = "123"

    private fun handleNotification(routingContext : RoutingContext) {
        val jsonBody = routingContext.bodyAsJsonArray
        val notificationList = jsonBody.list
                .map { ThirdPartyNotification(it as Map<String, String>, "collectionType", "ownerId") }

        GlobalScope.launch(vertx.delegate.dispatcher()) {
            // Retrieve data in coroutine and reply immediately
            notificationService.getUpdatedData(notificationList)
        }

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
        val parameters = OAuth2RouterParameters("http://localhost:8180/login", "", "activity nutrition heartrate profile settings sleep weight")
        val userDataService = VertxUserServiceImpl(vertx.delegate)
        val authRouter = FitbitOAuth2Router(vertx, oauth2, parameters, userDataService).getRouter()
        router.mountSubRouter("/", authRouter)

        vertx.createHttpServer().requestHandler(router).listen(8180)
    }
}
