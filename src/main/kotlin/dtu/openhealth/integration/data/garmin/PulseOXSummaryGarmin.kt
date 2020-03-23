package dtu.openhealth.integration.data.garmin

import dtu.openhealth.integration.common.exception.NoMappingFoundException
import org.openmhealth.schema.domain.omh.Measure

data class PulseOXSummaryGarmin(
        val userId: String? = null,
        val userAccessToken: String? = null,
        val summaryId: String? = null,
        val calendarDate: String? = null,
        val startTimeInSeconds: Float? = null,
        val startTimeOffsetInSeconds: Int? = null,
        val durationInSeconds: Int? = null,
        val timeOffsetSpo2Values: Map<String, Int>? = null,
        val onDemand: Boolean? = null
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        throw NoMappingFoundException("No mapping found for this type: ${this.javaClass}")
    }
}

