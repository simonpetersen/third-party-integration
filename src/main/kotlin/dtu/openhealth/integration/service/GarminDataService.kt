package dtu.openhealth.integration.service

import dtu.openhealth.integration.data.garmin.BodyCompositionSummaryGarmin

interface GarminDataService {
    fun saveBodyCompositionSummaryData(bodyCompositionSummaryGarmin: BodyCompositionSummaryGarmin)
}
