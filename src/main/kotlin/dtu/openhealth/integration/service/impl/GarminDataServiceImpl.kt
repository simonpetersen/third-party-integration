package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.data.garmin.BodyCompositionSummaryGarmin
import dtu.openhealth.integration.data.garmin.DailySummaryGarmin
import dtu.openhealth.integration.data.garmin.GarminData
import dtu.openhealth.integration.service.GarminDataService
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
