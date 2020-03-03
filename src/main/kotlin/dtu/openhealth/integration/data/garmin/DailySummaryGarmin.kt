package dtu.openhealth.integration.data.garmin

class DailySummaryGarmin(private val uploadStartTimeInSeconds: Int, private val uploadEndTimeInSeconds: Int,
                         private val summaryId: String, private val calendarDate: String,
                         private val startTimeInSeconds: Int, private val startTimeOffsetInSeconds: Int,
                         private val activityType: String, private val durationInSeconds: Int, private val steps: Int,
                         private val distanceInMeters: Float, private val activeTimeInSeconds: Int,
                         private val activeKilocalories: Int, private val bmrKilocalories: Int,
                         private val consumedCalories: Int, private val moderateIntensityDurationInSeconds: Int,
                         private val vigorousIntensityDurationInSeconds: Int, private val floorsClimbed: Int,
                         private val minHeartRateInBeatsPerMinute: Int, private val averageHeartRateInBeatsPerMinute: Int,
                         private val maxHeartRateInBeatsPerMinute: Int, private val restingHeartRateInBeatsPerMinute: Int,
                         private val timeOffsetHeartRateSamples: Map<String, Int>, private val averageStressLevel: Int,
                         private val maxStressLevel: Int, private val stressDurationInSeconds: Int,
                         private val restStressDurationInSeconds: Int, private val activityStressDurationInSeconds: Int,
                         private val lowStressDurationInSeconds: Int, private val mediumStressDurationInSeconds: Int,
                         private val highStressDurationInSeconds: Int, private val stressQualifier: String,
                         private val stepsGoal: Int, private val netKilocaloriesGoal: Int,
                         private val intensityDurationGoalInSeconds: Int, private val floorsClimbedGoal: Int
)
