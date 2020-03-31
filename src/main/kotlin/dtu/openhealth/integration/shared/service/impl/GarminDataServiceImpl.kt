package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.data.ThirdPartyData
import dtu.openhealth.integration.shared.service.GarminDataService
import org.openmhealth.schema.domain.omh.Measure
import org.springframework.stereotype.Service

@Service
class GarminDataServiceImpl: GarminDataService {
    override fun saveDataToOMH(thirdPartyData: ThirdPartyData) {
        val measures: List<Measure> = thirdPartyData.mapToOMH()
        println(measures)
        //TODO Send data to Kafka publisher
    }
}
