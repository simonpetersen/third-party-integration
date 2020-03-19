package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.Measure

data class ActivitySummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val startTimeInSeconds: Int,
        val startTimeOffsetInSeconds: Int,
        val activityType: String,
        val durationInSeconds: Int,
        val averageBikeCadenceInRoundsPerMinute: Float,
        val averageHeartRateInBeatsPerMinute: Int,
        val averageRunCadenceInStepsPerMinute: Float,
        val averageSpeedInMetersPerSecond: Float,
        val averageSwimCadenceInStrokesPerMinute: Float,
        val averagePaceInMinutesPerKilometer: Float,
        val activeKilocalories: Int,
        val deviceName: String,
        val distanceInMeters: Float,
        val maxBikeCadenceInRoundsPerMinute: Float,
        val maxHeartRateInBeatsPerMinute: Float,
        val maxPaceInMinutesPerKilometer: Float,
        val maxRunCadenceInStepsPerMinute: Float,
        val maxSpeedInMetersPerSecond: Float,
        val numberOfActiveLengths: Int,
        val startingLatitudeInDegree: Float,
        val startingLongitudeInDegree: Float,
        val steps: Int,
        val totalElevationGainInMeters: Float,
        val totalElevationLossInMeters: Float,
        val isParent: Boolean,
        val parentSummaryId: Int,
        val manual: Boolean
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
