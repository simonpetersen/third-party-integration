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
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")

    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())
}
