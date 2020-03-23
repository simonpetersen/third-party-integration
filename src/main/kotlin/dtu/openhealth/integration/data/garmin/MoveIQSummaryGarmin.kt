package dtu.openhealth.integration.data.garmin

import dtu.openhealth.integration.common.exception.NoMappingFoundException
import org.openmhealth.schema.domain.omh.Measure

data class MoveIQSummaryGarmin(
        val userId: String? = null,
        val userAccessToken: String? = null,
        val summaryId: String? = null,
        val calendarDate: String? = null, //Date
        val startTimeInSeconds: Float? = null,
        val startTimeOffsetInSeconds: Int? = null,
        val durationInSeconds: Int? = null,
        val activityType: String? = null,
        val activitySubType: String? = null
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        throw NoMappingFoundException("No mapping found for this type: ${this.javaClass}")
    }
}

