package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.data.garmin.BodyCompositionSummaryGarmin
import dtu.openhealth.integration.service.GarminDataService
import org.springframework.stereotype.Service

@Service
class GarminDataServiceImpl: GarminDataService {

    override fun saveBodyCompositionSummaryData(bodyCompositionSummaryGarmin: BodyCompositionSummaryGarmin) {
        print(bodyCompositionSummaryGarmin)
    }
}
