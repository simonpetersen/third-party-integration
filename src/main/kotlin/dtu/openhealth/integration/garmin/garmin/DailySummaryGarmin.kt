package dtu.openhealth.integration.garmin.garmin

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.serialization.OffsetDateTimeSerializer
import kotlinx.serialization.Serializer
import org.openmhealth.schema.domain.omh.*
import java.sql.Time

data class DailySummaryGarmin(
    val userId: String? = null,
    val userAccessToken: String? = null,
    val summaryId: String? = null,
    val calendarDate: String? = null,
    val startTimeInSeconds: Int? = null,
    val startTimeOffsetInSeconds: Int? = null,
    val activityType: String? = null,
    val durationInSeconds: Int? = null,
    val steps: Int? = null,
    val distanceInMeters: Float? = null,
    val activeTimeInSeconds: Int? = null,
    val activeKilocalories: Int? = null,
    val bmrKilocalories: Int? = null,
    val consumedCalories: Int? = null,
    val moderateIntensityDurationInSeconds: Int? = null,
    val vigorousIntensityDurationInSeconds: Int? = null,
    val floorsClimbed: Int? = null,
    val minHeartRateInBeatsPerMinute: Int? = null,
    val averageHeartRateInBeatsPerMinute: Int? = null,
    val maxHeartRateInBeatsPerMinute: Int? = null,
    val restingHeartRateInBeatsPerMinute: Int? = null,
    val timeOffsetHeartRateSamples: Map<String, Int>? = null,
    val averageStressLevel: Int? = null,
    val maxStressLevel: Int? = null,
    val stressDurationInSeconds: Int? = null,
    val restStressDurationInSeconds: Int? = null,
    val activityStressDurationInSeconds: Int? = null,
    val lowStressDurationInSeconds: Int? = null,
    val mediumStressDurationInSeconds: Int? = null,
    val highStressDurationInSeconds: Int? = null,
    val stressQualifier: String? = null,
    val stepsGoal: Int? = null,
    val netKilocaloriesGoal: Int? = null,
    val intensityDurationGoalInSeconds: Int? = null,
    val floorsClimbedGoal: Int? = null
): GarminData() {
    override fun mapToOMH(): List<OmhDTO> {
        val steps = steps?.let {
            StepCount2.Builder(
                    it.toBigDecimal(), getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        val calories = bmrKilocalories?.let {
            CaloriesBurned2.Builder(
                    KcalUnitValue(KcalUnit.KILOCALORIE, (it + activeKilocalories!!).toBigDecimal()),
                    getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        val heartRate = averageHeartRateInBeatsPerMinute?.let {
            HeartRate.Builder(TypedUnitValue(HeartRateUnit.BEATS_PER_MINUTE, it.toBigDecimal()))
                    .setEffectiveTimeFrame(getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        return listOf(OmhDTO(userId = userId, stepCount2 = steps, caloriesBurned2 = calories, heartRate = heartRate))
    }
}

