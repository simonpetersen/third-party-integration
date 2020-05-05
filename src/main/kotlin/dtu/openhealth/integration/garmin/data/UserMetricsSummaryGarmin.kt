package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.exception.NoMappingFoundException
import kotlinx.serialization.Serializable

@Serializable
data class UserMetricsSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val calendarDate: String? = null, //Date
        val vo2Max: Float? = null,
        val fitnessAge: Int? = null
): GarminData() {
    override fun mapToOMH(parameters: Map<String,String>): OmhDTO {
        throw NoMappingFoundException("No mapping found for this type: ${this.javaClass}")
    }
}
