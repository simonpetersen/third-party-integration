package dtu.openhealth.integration

import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.impl.OAuth2TokenRefreshServiceImpl
import dtu.openhealth.integration.shared.service.impl.TestUserServiceImpl
import dtu.openhealth.integration.shared.service.impl.VertxUserServiceImpl
import io.vertx.reactivex.core.Vertx
import dtu.openhealth.integration.shared.verticle.MainVerticle
import dtu.openhealth.integration.shared.web.parameters.OAuth2RefreshParameters
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.kotlin.ext.auth.oauth2.oAuth2ClientOptionsOf
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth


class IntegrationApplication

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())

    /*
    val parameters = OAuth2RefreshParameters(
            host = "api.fitbit.com",
            refreshPath = "/oauth2/token",
            clientId = "22B9BW", clientSecret = "51cfa2f4874bafc7eda72aaca3a89011")
    val webClient = WebClient.create(vertx)
    val user = User("89NGPS",
            "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMkI5QlciLCJzdWIiOiI4OU5HUFMiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJ3aHIgd3BybyB3bnV0IHdzbGUgd3dlaSB3c29jIHdzZXQgd2FjdCIsImV4cCI6MTU4NzA1MDExOCwiaWF0IjoxNTg3MDIxMzE4fQ.dthqZHNoG6fYr4NKFYV8xmlvpnSiWBgWiM9XNBAobSU",
            "fdb44c8b3d830389bba7cff83acd316e499e14e0dc2317ebe481a710e0a9170e")

    val refreshTokenService = OAuth2TokenRefreshServiceImpl(webClient, parameters, TestUserServiceImpl())
    refreshTokenService.refreshToken(user) { println(it) }
     */
}
