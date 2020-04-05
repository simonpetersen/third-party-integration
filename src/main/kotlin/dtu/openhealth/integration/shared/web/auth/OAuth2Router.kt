package dtu.openhealth.integration.shared.web.auth

import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext

class OAuth2Router(private val vertx: Vertx, private val oauth2: OAuth2Auth, private val parameters: OAuth2Parameters) : AuthenticationRouter {

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

        oauth2.authenticate(tokenConfig) { res ->
            if (res.failed()) {
                System.err.println("Access Token Error: ${res.cause()}")
            } else {
                val token = res.result()
                println(token.principal())
            }
        }

        routingContext
                .response()
                .putHeader("Location", successUri)
                .setStatusCode(302)
                .end()
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
}