package dtu.openhealth.integration.verticle

import io.vertx.core.AbstractVerticle

class MainVerticle : AbstractVerticle() {

    override fun start() {
        vertx.deployVerticle(FitbitVerticle())
        vertx.deployVerticle(GarminVerticle())
    }
}