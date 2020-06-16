package dtu.openhealth.integration.shared.service.push

import dtu.openhealth.integration.shared.model.AThirdPartyData

interface IThirdPartyPushService {
    fun saveDataToOMH(thirdPartyData: AThirdPartyData)
}
