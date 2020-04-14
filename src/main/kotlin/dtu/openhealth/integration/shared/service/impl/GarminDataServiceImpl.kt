package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.garmin.GarminVerticle
import dtu.openhealth.integration.kafka.publisher.KafkaProducerService
import dtu.openhealth.integration.shared.data.ThirdPartyData
import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.service.GarminDataService
import io.vertx.core.logging.LoggerFactory
import org.openmhealth.schema.domain.omh.Measure
import org.springframework.stereotype.Service

@Service
class GarminDataServiceImpl(private val kafkaProducerService: KafkaProducerService) : GarminDataService {
    private val LOGGER = LoggerFactory.getLogger(GarminDataServiceImpl::class.java)
    override fun saveDataToOMH(thirdPartyData: ThirdPartyData) {
        val omhDTOList = thirdPartyData.mapToOMH()
        omhDTOList.forEach {
            LOGGER.info("Sending data to kafka producer: $it")
            kafkaProducerService.sendOmhData(it)
        }
    }
}
