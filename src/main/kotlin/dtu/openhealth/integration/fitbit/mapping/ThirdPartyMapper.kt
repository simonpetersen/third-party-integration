package dtu.openhealth.integration.fitbit.mapping

import dtu.openhealth.integration.data.ThirdPartyData
import org.openmhealth.schema.domain.omh.Measure

// Interface implemented for each thirdParty.
interface ThirdPartyMapper {
    fun mapData(thirdPartyData: ThirdPartyData): List<Measure>
}