package dtu.openhealth.integration.shared.web.auth

import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.UserDataService
import dtu.openhealth.integration.shared.web.parameters.OAuth2RouterParameters
import io.vertx.core.AsyncResult
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class OAuth2Router(private val vertx: Vertx, private val oauth2: OAuth2Auth,
                   private val parameters: OAuth2RouterParameters,
                   private val userDataService: UserDataService
)
    : AuthenticationRouter {

    override fun getRouter() : Router {
        val router = Router.router(vertx)
        router.get("/auth/:userId").handler{ handleAuthRedirect(it) }
        router.get("/login").handler { handleAuthCallback(it) }
        router.get("/success").handler { handleSuccessfulAuthentication(it) }

        return router
    }

    private fun handleAuthCallback(routingContext: RoutingContext) {
        val code = routingContext.request().getParam("code")
        val userId = routingContext.request().getParam("state")
        val successUri = "http://localhost:8080/success"

        var tokenConfig = json {
            obj(
                    "code" to code,
                    "redirect_uri" to parameters.redirectUri,
                    "state" to userId
            )
        }

        oauth2.authenticate(tokenConfig) { authorizationSuccessful(it, userId, routingContext) }
    }

    private fun handleSuccessfulAuthentication(routingContext: RoutingContext) {
        routingContext.response().end("User authenticated. Back to you CARP.")
    }

    private fun handleAuthRedirect(routingContext: RoutingContext) {
        val userId = routingContext.request().getParam("userId")
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

    private fun authorizationSuccessful(ar : AsyncResult<io.vertx.reactivex.ext.auth.User>, userId: String, routingContext: RoutingContext) {
        if (ar.failed()) {
            System.err.println("Access Token Error: ${ar.cause()}")
        } else {
            GlobalScope.launch {
                val jsonToken = ar.result().principal()
                val extUserId = jsonToken.getString("user_id")
                val accessToken = jsonToken.getString("access_token")
                val refreshToken = jsonToken.getString("refresh_token")
                val expiresIn = jsonToken.getLong("expires_in")
                val expireDateTime = LocalDateTime.now().plusSeconds(expiresIn)

                val user = User(userId, extUserId, accessToken, refreshToken, expireDateTime)
                userDataService.createUser(user)

                routingContext.response()
                        .putHeader("Location", "http://localhost:8080/success")
                        .setStatusCode(302)
                        .end()
            }
        }
    }
}