package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.exception.NoMappingFoundException
import kotlinx.serialization.Serializable

@Serializable
data class MoveIQSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val calendarDate: String? = null, //Date
        val startTimeInSeconds: Float? = null,
        val startTimeOffsetInSeconds: Int? = null,
        val durationInSeconds: Int? = null,
        val activityType: String? = null,
        val activitySubType: String? = null
): GarminData() {
    override fun mapToOMH(): OmhDTO {
        throw NoMappingFoundException("No mapping found for this type: ${this.javaClass}")
    }
}

