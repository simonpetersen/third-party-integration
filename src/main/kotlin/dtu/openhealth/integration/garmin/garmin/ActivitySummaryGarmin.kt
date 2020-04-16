package dtu.openhealth.integration.garmin.garmin

import dtu.openhealth.integration.shared.dto.OmhDTO
import org.openmhealth.schema.domain.omh.*
import java.lang.RuntimeException

data class ActivitySummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
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
    override fun mapToOMH(): OmhDTO {
        val physicalActivity = activityType?.let {
                PhysicalActivity.Builder(it)
                    .setDistance(LengthUnitValue(LengthUnit.METER, distanceInMeters?.toBigDecimal()))
                    .setCaloriesBurned(KcalUnitValue(KcalUnit.KILOCALORIE, activeKilocalories?.toBigDecimal()))
                    .setEffectiveTimeFrame(getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        return OmhDTO(userId = userId, physicalActivities = listOf(physicalActivity!!))
    }
}
