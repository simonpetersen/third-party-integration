package dtu.openhealth.integration.shared.web.auth

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1RequestToken
import dtu.openhealth.integration.shared.service.UserDataService
import dtu.openhealth.integration.shared.web.parameters.OAuth1RouterParameters
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import java.security.SecureRandom


class OAuth1Router(private val vertx: Vertx, private val parameters: OAuth1RouterParameters,
                   private val userDataService: UserDataService,
                   private val requestTokenSecrets : MutableMap<String, String> = mutableMapOf())
    : AuthenticationRouter {

    //private val signatureMethod = "HMAC-SHA1"

    override fun getRouter(): Router {
        val router = Router.router(vertx)

        router.get("/auth/:userId").blockingHandler { handleAuthRedirect(it) }
        router.get("/callback/:userId").blockingHandler { handleAuthCallback(it) }

        return router
    }

    private fun handleAuthCallback(routingContext: RoutingContext) {
        val token = routingContext.request().getParam("oauth_token")
        val verifier = routingContext.request().getParam("oauth_verifier")
        val extUserId = routingContext.request().getParam("userId")

        val requestTokenSecret = requestTokenSecrets[extUserId]
        if (requestTokenSecret != null) {
            val oauthService = ServiceBuilder(parameters.consumerKey)
                    .apiSecret(parameters.consumerSecret)
                    .build(parameters.api)

            val requestTokenOwn = OAuth1RequestToken(token, requestTokenSecret)
            val accessToken = oauthService.getAccessToken(requestTokenOwn, verifier)
            requestTokenSecrets.remove(extUserId)

            // TODO: Save the new token
            println("${accessToken.token}  +  ${accessToken.tokenSecret}")

            routingContext.response()
                    .end("Token authorization finished")
        }
    }

    private fun handleAuthRedirect(routingContext: RoutingContext) {
        val userId = routingContext.request().getParam("userId")
        val callbackUri = "${parameters.callbackUri}/$userId"
        /*
        val authorizationUrl = ""

        val timestamp = Date().time
        var authorizationHeader = "oauth_consumer_key=${parameters.consumerKey}&"
        authorizationHeader += "oauth_nonce=&${getNonce()}"
        authorizationHeader += "oauth_signature_method=$signatureMethod&"
        authorizationHeader += "oauth_timestamp=$timestamp&oauth_version=1.0"

        val secret = "${parameters.consumerSecret}&"
        val signature = SignatureCalculator.hmacSha1(secret, authorizationHeader)

        authorizationHeader += "&oauth_signature=$signature"

        webClient.post(443, parameters.host, parameters.requestTokenPath)
                .ssl(true)
                .putHeader("Authorization", authorizationHeader)
                .expect(ResponsePredicate.SC_SUCCESS)
                .send {
                    if (it.failed()) {
                        println("Obtaining unhandled token failed: ${it.cause()}")
                    }
                    else {
                        println("Obtaining token succeeded: ${it.result()}")
                    }
                }

         */

        val oauthService = ServiceBuilder(parameters.consumerKey)
                .apiSecret(parameters.consumerSecret)
                .callback(callbackUri)
                .build(parameters.api)

        val requestToken = oauthService.requestToken
        requestTokenSecrets[userId] = requestToken.tokenSecret

        val authorizationUrl = oauthService.getAuthorizationUrl(requestToken)
        val authUrl = "$authorizationUrl&oauth_callback=$callbackUri"
        routingContext.response()
                .putHeader("Location", authUrl)
                .setStatusCode(302)
                .end()
    }

    private fun getNonce(): String {
        val secureRandom = SecureRandom()
        val sb = StringBuilder()
        for (i in 0..15) {
            sb.append(secureRandom.nextInt())
        }

        return sb.toString()
    }
}