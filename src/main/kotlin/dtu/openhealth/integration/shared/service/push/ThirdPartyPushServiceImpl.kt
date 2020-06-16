package dtu.openhealth.integration.shared.service.push

import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.AThirdPartyData
import io.vertx.core.logging.LoggerFactory

class ThirdPartyPushServiceImpl(
        private val kafkaProducerService: IKafkaProducerService
): IThirdPartyPushService {

    private val logger = LoggerFactory.getLogger(ThirdPartyPushServiceImpl::class.java)

    override fun saveDataToOMH(thirdPartyData: AThirdPartyData)
    {
        val omhDTO = thirdPartyData.mapToOMH()
        logger.info("Sending data to kafka producer: $omhDTO")
        kafkaProducerService.sendOmhData(omhDTO)
    }
}
