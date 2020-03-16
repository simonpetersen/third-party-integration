package dtu.openhealth.integration.data.garmin

import java.util.*

data class DailySummaryGarmin(
        val userId: String,
        val userAccessToken: UUID,
        val summaryId: String,
        val calendarDate: String,
        val startTimeInSeconds: Int,
        val startTimeOffsetInSeconds: Int,
        val activityType: String,
        val durationInSeconds: Int,
        val steps: Int,
        val distanceInMeters: Float,
        val activeTimeInSeconds: Int,
        val activeKilocalories: Int,
        val bmrKilocalories: Int,
        val consumedCalories: Int,
        val moderateIntensityDurationInSeconds: Int,
        val vigorousIntensityDurationInSeconds: Int,
        val floorsClimbed: Int,
        val minHeartRateInBeatsPerMinute: Int,
        val averageHeartRateInBeatsPerMinute: Int,
        val maxHeartRateInBeatsPerMinute: Int,
        val restingHeartRateInBeatsPerMinute: Int,
        val timeOffsetHeartRateSamples: Map<String, Int>,
        val averageStressLevel: Int,
        val maxStressLevel: Int,
        val stressDurationInSeconds: Int,
        val restStressDurationInSeconds: Int,
        val activityStressDurationInSeconds: Int,
        val lowStressDurationInSeconds: Int,
        val mediumStressDurationInSeconds: Int,
        val highStressDurationInSeconds: Int,
        val stressQualifier: String,
        val stepsGoal: Int,
        val netKilocaloriesGoal: Int,
        val intensityDurationGoalInSeconds: Int,
        val floorsClimbedGoal: Int
): GarminData()

