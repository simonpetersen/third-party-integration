package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.Measure

data class ThirdPartyDailySummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val startTimeInSeconds: Int,
        val startTimeOffsetInSeconds: Int,
        val activityType: String,
        val durationInSeconds: Int,
        val steps: Int,
        val distanceInMeters: Float,
        val activeTimeInSeconds: Int,
        val activeKilocalories: Int,
        val bmrKilocalories: Int,
        val moderateIntensityDurationInSeconds: Int,
        val vigorousIntensityDurationInSeconds: Int,
        val floorsClimbed: Int,
        val minHeartRateInBeatsPerMinute: Int,
        val averageHeartRateInBeatsPerMinute: Int,
        val maxHeartRateInBeatsPerMinute: Int,
        val timeOffsetHeartRateSamples: Map<String, Int>,
        val source: String
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

