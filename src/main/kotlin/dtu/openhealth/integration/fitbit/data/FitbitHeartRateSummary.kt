package dtu.openhealth.integration.fitbit.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.serialization.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.HeartRate
import org.openmhealth.schema.domain.omh.HeartRateUnit
import org.openmhealth.schema.domain.omh.TemporalRelationshipToPhysicalActivity
import org.openmhealth.schema.domain.omh.TypedUnitValue
import java.time.LocalDate

@Serializable
data class FitbitHeartRateSummary(
        @SerialName("activities-heart") val heartRates: List<FitbitActivitiesHeart>
): FitbitData() {
    override fun mapToOMH(parameters: Map<String,String>): OmhDTO
    {
        val fitbitUserId = parameters[FitbitConstants.UserParameterTag]
        return heartRates.map { it.mapToOMH(fitbitUserId) }.first()
    }
}

@Serializable
data class FitbitActivitiesHeart(
        @Serializable(with = LocalDateSerializer::class) val dateTime: LocalDate,
        val value: FitbitHeartRateValues
) {
    fun mapToOMH(fitbitUserId: String?): OmhDTO
    {
        return OmhDTO(extUserId = fitbitUserId, date = dateTime, heartRate = value.mapToOMH())
    }
}

@Serializable
data class FitbitHeartRateValues(
        val customHeartRateZones: List<FitbitHeartRateZone>,
        val heartRateZones: List<FitbitHeartRateZone>,
        val restingHeartRate: Long? = null
) {
    fun mapToOMH(): HeartRate?
    {
        if (restingHeartRate == null) {
            return null
        }

        return HeartRate.Builder(TypedUnitValue(HeartRateUnit.BEATS_PER_MINUTE, restingHeartRate))
                .setTemporalRelationshipToPhysicalActivity(TemporalRelationshipToPhysicalActivity.AT_REST)
                .build()
    }
}