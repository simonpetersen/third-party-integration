package dtu.openhealth.integration.shared.verticle.main

import dtu.openhealth.integration.fitbit.FitbitPullVerticle
import dtu.openhealth.integration.kafka.producer.impl.KafkaProducerServiceImpl
import dtu.openhealth.integration.shared.service.data.usertoken.UserTokenDataServiceImpl
import dtu.openhealth.integration.shared.verticle.omh.OmhConsumerVerticle
import dtu.openhealth.integration.shared.verticle.web.WebServerVerticle
import io.vertx.reactivex.core.AbstractVerticle

class MainVerticle : AbstractVerticle() {

    override fun start()
    {
        val userTokenDataService = UserTokenDataServiceImpl(vertx.delegate)
        val kafkaProducerService = KafkaProducerServiceImpl(vertx)
        vertx.deployVerticle(WebServerVerticle(userTokenDataService, kafkaProducerService))
        vertx.deployVerticle(FitbitPullVerticle(userTokenDataService, kafkaProducerService))
        vertx.deployVerticle(OmhConsumerVerticle(userTokenDataService))
    }

}
