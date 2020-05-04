package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.exception.InvalidActivityNameException
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*

@Serializable
data class EpochSummaryGarmin(
    val userId: String,
    val userAccessToken: String,
    val summaryId: String,
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

        val localDate = getLocalDate(startTimeInSeconds, startTimeOffsetInSeconds)

        return OmhDTO(extUserId = userAccessToken, date = localDate,
                stepCount2 = steps, physicalActivities = listOf(activity!!))
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

