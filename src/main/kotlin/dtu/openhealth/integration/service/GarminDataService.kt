package dtu.openhealth.integration.service

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.data.garmin.BodyCompositionSummaryGarmin
import dtu.openhealth.integration.data.garmin.DailySummaryGarmin
import dtu.openhealth.integration.data.garmin.GarminData

interface GarminDataService {
    fun saveDataToOMH(thirdPartyData: ThirdPartyData)
}
