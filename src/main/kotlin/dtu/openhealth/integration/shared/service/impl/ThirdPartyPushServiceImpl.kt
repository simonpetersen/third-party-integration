package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.kafka.producer.KafkaProducerService
import dtu.openhealth.integration.shared.model.ThirdPartyData
import dtu.openhealth.integration.shared.service.ThirdPartyPushService
import io.vertx.core.logging.LoggerFactory

class ThirdPartyPushServiceImpl(private val kafkaProducerService: KafkaProducerService) : ThirdPartyPushService {

    private val logger = LoggerFactory.getLogger(ThirdPartyPushServiceImpl::class.java)

    override fun saveDataToOMH(thirdPartyData: ThirdPartyData) {
        val omhDTO = thirdPartyData.mapToOMH()
        logger.info("Sending data to kafka producer: $omhDTO")
        kafkaProducerService.sendOmhData(omhDTO)
    }
}
