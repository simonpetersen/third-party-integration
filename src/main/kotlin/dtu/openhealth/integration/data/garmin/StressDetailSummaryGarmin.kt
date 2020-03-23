package dtu.openhealth.integration.data.garmin

import dtu.openhealth.integration.common.exception.NoMappingFoundException
import org.openmhealth.schema.domain.omh.Measure

data class StressDetailSummaryGarmin(
        val userId: String? = null,
        val userAccessToken: String? = null,
        val summaryId: String? = null,
        val startTimeInSeconds: Int? = null,
        val startTimeOffsetInSeconds: Int? = null,
        val durationInSeconds: Int? = null,
        val calendarDate: String? = null, //Date
        val timeOffsetStressLevelValues: Map<String, Int>? = null,
        val timeOffsetBodyBatteryDetails: Map<String, Int>? = null
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        throw NoMappingFoundException("No mapping found for this type: ${this.javaClass}")
    }
}

