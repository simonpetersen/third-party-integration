package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.*

data class ActivitySummaryGarmin(
        val userId: String? = null,
        val userAccessToken: String? = null,
        val summaryId: String? = null,
        val startTimeInSeconds: Int? = null,
        val startTimeOffsetInSeconds: Int? = null,
        val activityType: String? = null,
        val durationInSeconds: Int? = null,
        val averageBikeCadenceInRoundsPerMinute: Float? = null,
        val averageHeartRateInBeatsPerMinute: Int? = null,
        val averageRunCadenceInStepsPerMinute: Float? = null,
        val averageSpeedInMetersPerSecond: Float? = null,
        val averageSwimCadenceInStrokesPerMinute: Float? = null,
        val averagePaceInMinutesPerKilometer: Float? = null,
        val activeKilocalories: Int? = null,
        val deviceName: String? = null,
        val distanceInMeters: Float? = null,
        val maxBikeCadenceInRoundsPerMinute: Float? = null,
        val maxHeartRateInBeatsPerMinute: Float? = null,
        val maxPaceInMinutesPerKilometer: Float? = null,
        val maxRunCadenceInStepsPerMinute: Float? = null,
        val maxSpeedInMetersPerSecond: Float? = null,
        val numberOfActiveLengths: Int? = null,
        val startingLatitudeInDegree: Float? = null,
        val startingLongitudeInDegree: Float? = null,
        val steps: Int? = null,
        val totalElevationGainInMeters: Float? = null,
        val totalElevationLossInMeters: Float? = null,
        val isParent: Boolean? = null,
        val parentSummaryId: Int? = null,
        val manual: Boolean? = null
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        val measures = mutableListOf<Measure>()
        activityType?.let {
            measures.add(PhysicalActivity.Builder(it)
                    .setDistance(LengthUnitValue(LengthUnit.METER, distanceInMeters?.toBigDecimal()))
                    .setCaloriesBurned(KcalUnitValue(KcalUnit.KILOCALORIE, activeKilocalories?.toBigDecimal()))
                    .setEffectiveTimeFrame(getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build())
        }
        return measures
    }
}
