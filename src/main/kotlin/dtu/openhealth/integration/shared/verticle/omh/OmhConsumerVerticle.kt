package dtu.openhealth.integration.shared.verticle.omh

import dtu.openhealth.integration.kafka.consumer.KafkaConsumer
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.data.omh.OmhDataServiceImpl
import dtu.openhealth.integration.shared.service.omh.OmhServiceImpl
import io.vertx.reactivex.core.AbstractVerticle

class OmhConsumerVerticle(
        private val userTokenDataService: IUserTokenDataService
): AbstractVerticle() {

    override fun start()
    {
        val omhDataService = OmhDataServiceImpl(vertx)
        val omhService = OmhServiceImpl(userTokenDataService, omhDataService)
        val omhConsumer = KafkaConsumer(vertx, omhService)

        omhConsumer.consume()
    }
}
