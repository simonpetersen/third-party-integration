package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.kafka.producer.KafkaProducerService
import dtu.openhealth.integration.shared.data.ThirdPartyData
import dtu.openhealth.integration.shared.service.GarminDataService
import io.vertx.core.logging.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GarminDataServiceImpl(private val kafkaProducerService: KafkaProducerService) : GarminDataService {

    private val LOGGER = LoggerFactory.getLogger(GarminDataServiceImpl::class.java)

    override fun saveDataToOMH(thirdPartyData: ThirdPartyData) {
        val omhDTO = thirdPartyData.mapToOMH()
        LOGGER.info("Sending data to kafka producer: $omhDTO")
        kafkaProducerService.sendOmhData(omhDTO)
    }
}
