package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.Measure

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
        val sleepLevelsMap: Map<String, List<SleepTimeFrame>>,
        val validation: String,
        val timeOffsetSleepRespiration: Map<String, Float>,
        val timeOffsetSleepSpo2: Map<String, Int>
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


data class SleepTimeFrame(
        val startTimeInSeconds: Int,
        val endTimeInSeconds: Int
)
