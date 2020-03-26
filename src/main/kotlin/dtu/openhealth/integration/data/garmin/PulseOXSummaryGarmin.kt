package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.HeartRate
import org.openmhealth.schema.domain.omh.HeartRateUnit
import org.openmhealth.schema.domain.omh.Measure
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
    override fun mapToOMH(): List<Measure> {
        val measures = mutableListOf<Measure>()

        timeOffsetSpo2Values?.let {
            measures.add(HeartRate.Builder(
                    TypedUnitValue(HeartRateUnit.BEATS_PER_MINUTE, it.values.average()))
                    .setEffectiveTimeFrame(getTimeInterval(startTimeInSeconds?.toInt(), startTimeOffsetInSeconds, durationInSeconds))
                    .build())
        }

        return measures
    }
}

