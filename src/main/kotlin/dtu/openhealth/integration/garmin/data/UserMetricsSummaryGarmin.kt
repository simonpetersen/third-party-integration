package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.exception.NoMappingFoundException

data class UserMetricsSummaryGarmin(
        val userId: String? = null,
        val userAccessToken: String? = null,
        val summaryId: String? = null,
        val calendarDate: String? = null, //Date
        val vo2Max: Float? = null,
        val fitnessAge: Int? = null
): GarminData() {
    override fun mapToOMH(): OmhDTO {
        throw NoMappingFoundException("No mapping found for this type: ${this.javaClass}")
    }
}
