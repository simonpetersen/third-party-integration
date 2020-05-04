package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.HeartRate
import org.openmhealth.schema.domain.omh.HeartRateUnit
import org.openmhealth.schema.domain.omh.TypedUnitValue

@Serializable
data class PulseOXSummaryGarmin(
        val userId: String?,
        val userAccessToken: String,
        val summaryId: String,
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

        val localDate = getLocalDate(startTimeInSeconds?.toInt(), startTimeOffsetInSeconds)

        return OmhDTO(extUserId = userAccessToken, date = localDate, heartRate = heartRate)
    }
}

