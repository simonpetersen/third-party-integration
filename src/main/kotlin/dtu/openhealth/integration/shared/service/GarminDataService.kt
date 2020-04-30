package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.ThirdPartyData

interface GarminDataService {
    fun saveDataToOMH(thirdPartyData: ThirdPartyData)
}
