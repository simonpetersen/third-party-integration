package dtu.openhealth.integration.shared.service.push

import dtu.openhealth.integration.shared.model.ThirdPartyData

interface IThirdPartyPushService {
    fun saveDataToOMH(thirdPartyData: ThirdPartyData)
}
