package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.ThirdPartyData

interface ThirdPartyPushService {
    fun saveDataToOMH(thirdPartyData: ThirdPartyData)
}
