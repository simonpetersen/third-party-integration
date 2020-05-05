package dtu.openhealth.integration

import io.vertx.reactivex.core.Vertx
import dtu.openhealth.integration.shared.verticle.MainVerticle
import io.vertx.reactivex.config.ConfigRetriever


class IntegrationApplication

fun main() {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")

    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())
}
