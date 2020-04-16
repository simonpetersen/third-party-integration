package dtu.openhealth.integration.garmin.garmin

import dtu.openhealth.integration.shared.dto.OmhDTO
import org.openmhealth.schema.domain.omh.HeartRate
import org.openmhealth.schema.domain.omh.HeartRateUnit
import org.openmhealth.schema.domain.omh.TypedUnitValue

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
    override fun mapToOMH(): OmhDTO {
        val heartRate = timeOffsetSpo2Values?.let {
            HeartRate.Builder(TypedUnitValue(HeartRateUnit.BEATS_PER_MINUTE, it.values.average()))
                    .setEffectiveTimeFrame(getTimeInterval(startTimeInSeconds?.toInt(), startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        return OmhDTO(userId = userId, heartRate = heartRate)
    }
}

