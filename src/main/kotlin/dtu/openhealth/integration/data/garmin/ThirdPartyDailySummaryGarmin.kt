package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.*

data class ThirdPartyDailySummaryGarmin(
        val userId: String? = null,
        val userAccessToken: String? = null,
        val summaryId: String? = null,
        val startTimeInSeconds: Int? = null,
        val startTimeOffsetInSeconds: Int? = null,
        val activityType: String? = null,
        val durationInSeconds: Int? = null,
        val steps: Int? = null,
        val distanceInMeters: Float? = null,
        val activeTimeInSeconds: Int? = null,
        val activeKilocalories: Int? = null,
        val bmrKilocalories: Int? = null,
        val moderateIntensityDurationInSeconds: Int? = null,
        val vigorousIntensityDurationInSeconds: Int? = null,
        val floorsClimbed: Int? = null,
        val minHeartRateInBeatsPerMinute: Int? = null,
        val averageHeartRateInBeatsPerMinute: Int? = null,
        val maxHeartRateInBeatsPerMinute: Int? = null,
        val timeOffsetHeartRateSamples: Map<String, Int>? = null,
        val source: String? = null
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        val measures = mutableListOf<Measure>()

        steps?.let {
            measures.add(StepCount2.Builder(
                            it.toBigDecimal(), getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build())
        }

        bmrKilocalories?.let {
            measures.add(CaloriesBurned2.Builder(
                            KcalUnitValue(KcalUnit.KILOCALORIE, (it + activeKilocalories!!).toBigDecimal()),
                            getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build())
        }

        averageHeartRateInBeatsPerMinute?.let {
            measures.add(HeartRate.Builder(
                            TypedUnitValue(HeartRateUnit.BEATS_PER_MINUTE, averageHeartRateInBeatsPerMinute.toBigDecimal()))
                    .setEffectiveTimeFrame(getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build())
        }

        return measures
    }
}

