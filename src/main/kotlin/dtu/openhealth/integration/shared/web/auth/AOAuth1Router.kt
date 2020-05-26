package dtu.openhealth.integration.shared.web.auth

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.httpclient.HttpClient
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import com.github.scribejava.httpclient.ahc.AhcHttpClient
import com.github.scribejava.httpclient.ahc.AhcHttpClientConfig
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.web.parameters.OAuth1RouterParameters
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import java.util.*


abstract class AOAuth1Router(
        private val vertx: Vertx,
        private val parameters: OAuth1RouterParameters,
        private val userTokenDataService: IUserTokenDataService,
        private val requestTokenSecrets : MutableMap<String, String> = mutableMapOf()
): IAuthorizationRouter {

    private val logger = LoggerFactory.getLogger(AOAuth1Router::class.java)

    override fun getRouter(): Router
    {
        val router = Router.router(vertx)

        router.get("/auth").handler { handleAuthRedirectWithoutUserId(it) }
        router.get("/auth/:userId").handler { handleAuthRedirectWithUserId(it) }
        router.get("/callback/:userId").handler { handleAuthCallback(it) }
        router.get("/result").handler { handleResult(it) }

        return router
    }

    abstract fun getUserToken(userId: String, accessToken: String, tokenSecret: String): UserToken

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
        try
        {
            redirectToAuthPage(userId, routingContext)
        }
        catch (e: Exception)
        {
            val errorMsg = "Error when obtaining OAuth1 request token"
            logger.error(errorMsg, e)
            routingContext.response().end(errorMsg)
        }

    }

    private fun redirectToAuthPage(userId: String, routingContext: RoutingContext)
    {
        val callbackUri = "${parameters.callbackUri}/$userId"
        val oauthService = buildOAuthService(callbackUri)

        val requestToken = oauthService.requestToken
        requestTokenSecrets[userId] = requestToken.tokenSecret

        val authorizationUrl = oauthService.getAuthorizationUrl(requestToken)
        val authUrl = "$authorizationUrl&oauth_callback=$callbackUri"

        routingContext.response()
                .putHeader("Location", authUrl)
                .setStatusCode(302)
                .end()
    }

    private fun handleAuthCallback(routingContext: RoutingContext)
    {
        val userId = routingContext.request().getParam("userId")
        val requestTokenSecret = requestTokenSecrets[userId]

        if (requestTokenSecret != null)
        {
            getAccessToken(requestTokenSecret, userId, routingContext)
        }
        else
        {
            logger.error("No requestTokenSecret found for userId $userId")
            routingContext.response().end("No request token secret found")
        }
    }

    private fun getAccessToken(tokenSecret: String, userId: String, routingContext: RoutingContext)
    {
        try
        {
            callApiGetAccessToken(tokenSecret, userId, routingContext)
        }
        catch (e: Exception)
        {
            val errorMsg = "Error when obtaining OAuth1 access token"
            logger.error(errorMsg, e)
            routingContext.response().end(errorMsg)
        }
    }

    private fun callApiGetAccessToken(tokenSecret: String, userId: String, routingContext: RoutingContext)
    {
        val token = routingContext.request().getParam("oauth_token")
        val verifier = routingContext.request().getParam("oauth_verifier")
        val requestToken = OAuth1RequestToken(token, tokenSecret)
        val oauthService = buildOAuthService()
        val accessToken = oauthService.getAccessToken(requestToken, verifier)
        requestTokenSecrets.remove(userId)

        val userToken = getUserToken(userId, accessToken.token, accessToken.tokenSecret)
        userTokenDataService.insertUser(userToken)

        routingContext.response()
                .putHeader("Location", parameters.returnUri)
                .setStatusCode(302)
                .end()
    }

    private fun buildOAuthService(callbackUri: String? = null) : OAuth10aService
    {
        return ServiceBuilder(parameters.consumerKey)
                .apiSecret(parameters.consumerSecret)
                .callback(callbackUri)
                .httpClient(httpClient())
                .build(parameters.api)
    }

    private fun httpClient(): HttpClient {
        return AhcHttpClient(httpClientConfig())
    }

    private fun httpClientConfig(): AhcHttpClientConfig
    {
        return AhcHttpClientConfig(DefaultAsyncHttpClientConfig.Builder()
                .setMaxConnections(5)
                .setRequestTimeout(10000)
                .setPooledConnectionIdleTimeout(1000)
                .setReadTimeout(1000)
                .build())
    }

    private fun handleResult(routingContext: RoutingContext)
    {
        routingContext.response().end("Authorization successful")
    }

    private fun generateNewId(): String
    {
        val uniqueID = UUID.randomUUID().toString()
        return uniqueID.substring(0, 18)
    }
}