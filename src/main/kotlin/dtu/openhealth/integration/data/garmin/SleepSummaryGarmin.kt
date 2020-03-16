package dtu.openhealth.integration.data.garmin

data class SleepSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val calendarDate: String, //Date
        val startTimeInSeconds: Int,
        val startTimeOffsetInSeconds: Int,
        val durationInSeconds: Int,
        val unmeasurableSleepInSeconds: Int,
        val deepSleepDurationInSeconds: Int,
        val lightSleepDurationInSeconds: Int,
        val remSleepInSeconds: Int,
        val awakeDurationInSeconds: Int,
        val sleepLevelsMap: Map<String, List<TimeFrame>>,
        val validation: String,
        val timeOffsetSleepRespiration: Map<String, Float>,
        val timeOffsetSleepSpo2: Map<String, Int>
): GarminData()


data class TimeFrame(
        val startTimeInSeconds: Int,
        val endTimeInSeconds: Int
)
