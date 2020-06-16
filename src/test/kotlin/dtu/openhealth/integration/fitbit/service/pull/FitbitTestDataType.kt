package dtu.openhealth.integration.fitbit.service.pull

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.model.AThirdPartyData
import kotlinx.serialization.Serializable

@Serializable
data class FitbitTestDataType(
        val userId: String,
        val value: Int
): AThirdPartyData() {

    override fun mapToOMH(parameters: Map<String, String>): OmhDTO {
        return OmhDTO(extUserId = userId)
    }

}