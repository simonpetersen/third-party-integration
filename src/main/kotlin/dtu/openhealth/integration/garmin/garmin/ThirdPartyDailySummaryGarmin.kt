package dtu.openhealth.integration.garmin.garmin

import dtu.openhealth.integration.shared.dto.OmhDTO
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
    override fun mapToOMH(): OmhDTO {
        val stepCount = steps?.let {
            StepCount2.Builder(
                    it.toBigDecimal(), getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        val caloriesBurned = bmrKilocalories?.let {
            CaloriesBurned2.Builder(
                    KcalUnitValue(KcalUnit.KILOCALORIE, (it + activeKilocalories!!).toBigDecimal()),
                    getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        val heartRate = averageHeartRateInBeatsPerMinute?.let {
            HeartRate.Builder(
                    TypedUnitValue(HeartRateUnit.BEATS_PER_MINUTE, averageHeartRateInBeatsPerMinute.toBigDecimal()))
                    .setEffectiveTimeFrame(getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        return OmhDTO(userId = userId, stepCount2 = stepCount, caloriesBurned2 = listOf(caloriesBurned!!), heartRate = heartRate)
    }
}

