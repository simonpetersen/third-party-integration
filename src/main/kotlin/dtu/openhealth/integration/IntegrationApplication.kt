package dtu.openhealth.integration

import dtu.openhealth.integration.verticle.MainVerticle
import io.vertx.core.Vertx

class IntegrationApplication

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())
}
