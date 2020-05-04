package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*

@Serializable
data class ThirdPartyDailySummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
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

        val localDate = getLocalDate(startTimeInSeconds, startTimeOffsetInSeconds)

        return OmhDTO(extUserId = userAccessToken, date = localDate,
                stepCount2 = stepCount,
                caloriesBurned2 = caloriesBurned,
                heartRate = heartRate)
    }
}

