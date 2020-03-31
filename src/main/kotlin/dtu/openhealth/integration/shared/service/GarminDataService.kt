package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.data.ThirdPartyData

interface GarminDataService {
    fun saveDataToOMH(thirdPartyData: ThirdPartyData)
}
