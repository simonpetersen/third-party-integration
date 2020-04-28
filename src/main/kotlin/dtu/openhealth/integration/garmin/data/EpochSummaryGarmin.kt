package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.exception.InvalidActivityNameException
import org.openmhealth.schema.domain.omh.*

data class EpochSummaryGarmin(
    val userId: String? = null,
    val userAccessToken: String? = null,
    val summaryId: String? = null,
    val startTimeInSeconds: Int? = null,
    val startTimeOffsetInSeconds: Int? = null,
    val activityType: String? = null,
    val durationInSeconds: Int? = null,
    val activeTimeInSeconds: Int? = null,
    val steps: Int? = null,
    val distanceInMeters: Float? = null,
    val activeKilocalories: Int? = null,
    val met: Float? = null,
    val intensity: String? = null,
    val meanMotionIntensity: Float? = null,
    val maxMotionIntensity: Float? = null
): GarminData() {
    override fun mapToOMH(): OmhDTO {

        val steps = steps?.let {
            StepCount2.Builder(
                    it.toBigDecimal(), getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        val activity = activityType?.let {
            PhysicalActivity.Builder(it)
                    .setDistance(LengthUnitValue(LengthUnit.METER, distanceInMeters?.toBigDecimal()))
                    .setCaloriesBurned(KcalUnitValue(KcalUnit.KILOCALORIE, activeKilocalories?.toBigDecimal()))
                    .setEffectiveTimeFrame(getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, activeTimeInSeconds))
                    .setReportedActivityIntensity(getActivityName(intensity))
                    .build()
        }

        return OmhDTO(userId = userId, stepCount2 = steps, physicalActivities = listOf(activity!!))
    }

    private fun getActivityName(activityType: String?): PhysicalActivity.SelfReportedIntensity {
        return when (activityType) {
            "SEDENTARY" -> {
                PhysicalActivity.SelfReportedIntensity.LIGHT
            }
            "Active" -> {
                PhysicalActivity.SelfReportedIntensity.MODERATE
            }
            "HIGHLY_ACTIVE" -> {
                PhysicalActivity.SelfReportedIntensity.VIGOROUS
            }
            else -> throw InvalidActivityNameException("No activity mapping for activity: $activityType")
        }
    }
}

