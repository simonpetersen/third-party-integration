package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.kafka.KafkaProducerService
import dtu.openhealth.integration.shared.data.ThirdPartyData
import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.service.GarminDataService
import org.openmhealth.schema.domain.omh.Measure
import org.springframework.stereotype.Service

@Service
class GarminDataServiceImpl(private val kafkaProducerService: KafkaProducerService) : GarminDataService {
    override fun saveDataToOMH(thirdPartyData: ThirdPartyData) {
        val measures: List<Measure> = thirdPartyData.mapToOMH()
        val omhDTO = OmhDTO("1234", measures, 123455, 123456)
        kafkaProducerService.sendOmhData(omhDTO)
    }
}
