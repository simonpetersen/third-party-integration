package dtu.openhealth.integration.shared.verticle

import dtu.openhealth.integration.kafka.consumer.KafkaConsumer
import dtu.openhealth.integration.shared.service.UserDataService
import dtu.openhealth.integration.shared.service.impl.OmhDataServiceImpl
import dtu.openhealth.integration.shared.service.impl.OmhServiceImpl
import dtu.openhealth.integration.shared.service.impl.UserDataServiceImpl
import io.vertx.reactivex.core.AbstractVerticle

class OmhConsumerVerticle(private val userDataService: UserDataService) : AbstractVerticle() {

    override fun start() {
        val omhDataService = OmhDataServiceImpl(vertx.delegate)
        val omhService = OmhServiceImpl(userDataService, omhDataService)
        val omhConsumer = KafkaConsumer(vertx, omhService)

        omhConsumer.consume()
    }
}