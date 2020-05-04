package dtu.openhealth.integration

import dtu.openhealth.integration.kafka.consumer.KafkaConsumer

import io.vertx.reactivex.core.Vertx
import dtu.openhealth.integration.shared.verticle.MainVerticle


class IntegrationApplication

fun main() {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")

    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())
}
