package dtu.openhealth.integration.shared.web.auth

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import io.vertx.core.AsyncResult
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.auth.User
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import java.util.*

abstract class AOAuth2Router(
        private val vertx: Vertx,
        private val oauth2: OAuth2Auth,
        private val parameters: OAuth2RouterParameters,
        private val userTokenDataService: IUserTokenDataService
): IAuthorizationRouter {

    private val logger = LoggerFactory.getLogger(AOAuth2Router::class.java)

    override fun getRouter() : Router
    {
        val router = Router.router(vertx)
        router.get("/auth").handler{ handleAuthRedirectWithoutUserId(it) }
        router.get("/auth/:userId").handler{ handleAuthRedirectWithUserId(it) }
        router.get("/callback").handler { handleAuthCallback(it) }
        router.get("/success").handler { handleSuccessfulAuthentication(it) }

        return router
    }

    abstract fun createUser(userId: String, jsonObject: JsonObject): UserToken

    private fun handleAuthRedirectWithUserId(routingContext: RoutingContext)
    {
        val userId = routingContext.request().getParam("userId")
        handleAuthRedirect(userId, routingContext)
    }

    private fun handleAuthRedirectWithoutUserId(routingContext: RoutingContext)
    {
        val userId = generateNewId()
        handleAuthRedirect(userId, routingContext)
    }

    private fun handleAuthRedirect(userId: String, routingContext: RoutingContext)
    {
        val authorizationUri = oauth2.authorizeURL(json {
            obj(
                    "redirect_uri" to parameters.redirectUri,
                    "scope" to parameters.scope,
                    "state" to userId
            )
        })

        routingContext
                .response()
                .putHeader("Location", authorizationUri)
                .setStatusCode(302)
                .end()
    }

    private fun handleAuthCallback(routingContext: RoutingContext)
    {
        val code = routingContext.request().getParam("code")
        val userId = routingContext.request().getParam("state")

        var tokenConfig = json {
            obj(
                    "code" to code,
                    "redirect_uri" to parameters.redirectUri,
                    "state" to userId
            )
        }

        oauth2.authenticate(tokenConfig) {
            authorizationCompleted(it, userId, routingContext)
        }
    }

    private fun handleSuccessfulAuthentication(routingContext: RoutingContext)
    {
        routingContext.response().end("User authenticated.")
    }

    private fun authorizationCompleted(ar : AsyncResult<User>, userId: String, routingContext: RoutingContext)
    {
        if (ar.succeeded()) {
            val jsonToken = ar.result().principal()
            val user = createUser(userId, jsonToken)
            userTokenDataService.insertUser(user)

            routingContext.response()
                    .putHeader("Location", parameters.returnUri)
                    .setStatusCode(302)
                    .end()
        }
        else {
            logger.error(ar.cause())
        }
    }

    private fun generateNewId(): String
    {
        val uniqueID = UUID.randomUUID().toString()
        return uniqueID.substring(0, 18)
    }
}