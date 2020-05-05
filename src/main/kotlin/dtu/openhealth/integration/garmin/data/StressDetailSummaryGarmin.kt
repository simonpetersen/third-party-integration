package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.exception.NoMappingFoundException
import kotlinx.serialization.Serializable

@Serializable
data class StressDetailSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val startTimeInSeconds: Int? = null,
        val startTimeOffsetInSeconds: Int? = null,
        val durationInSeconds: Int? = null,
        val calendarDate: String? = null, //Date
        val timeOffsetStressLevelValues: Map<String, Int>? = null,
        val timeOffsetBodyBatteryDetails: Map<String, Int>? = null
): GarminData() {
    override fun mapToOMH(parameters: Map<String,String>): OmhDTO {
        throw NoMappingFoundException("No mapping found for this type: ${this.javaClass}")
    }
}

