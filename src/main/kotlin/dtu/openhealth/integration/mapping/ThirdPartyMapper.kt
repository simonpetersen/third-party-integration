package dtu.openhealth.integration.mapping

import dtu.openhealth.integration.data.omh.OpenMHealthData
import dtu.openhealth.integration.data.ThirdPartyData

// Interface implemented for each thirdParty.
interface ThirdPartyMapper {
    fun mapData(thirdPartyData: ThirdPartyData): OpenMHealthData
}