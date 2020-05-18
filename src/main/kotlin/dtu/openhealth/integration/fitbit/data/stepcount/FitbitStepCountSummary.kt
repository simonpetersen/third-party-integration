package dtu.openhealth.integration.fitbit.data.stepcount

import dtu.openhealth.integration.fitbit.data.heartrate.FitbitActivitiesHeart
import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.fitbit.data.FitbitData
import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.serialization.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*
import java.time.LocalDate
import java.time.ZoneOffset

@Serializable
data class FitbitStepCountSummary(
        @SerialName("activities-stepcount") val stepCounts: List<FitbitActivitiesHeart>
): FitbitData() {
    override fun mapToOMH(parameters: Map<String,String>): OmhDTO
    {
        val fitbitUserId = parameters[FitbitConstants.UserParameterTag]
        return stepCounts.map { it.mapToOMH(fitbitUserId) }.first()
    }
}

@Serializable
data class FitbitActivitiesStepCount(
        @Serializable(with = LocalDateSerializer::class) val dateTime: LocalDate,
        val stepCount: Long
) {
    fun mapToOMH(fitbitUserId: String?): OmhDTO
    {
        val startDateTime = dateTime.atStartOfDay().atOffset(ZoneOffset.UTC)
        val timeInterval = TimeInterval
                .ofStartDateTimeAndDuration(startDateTime, DurationUnitValue(DurationUnit.DAY,1))

        // Step count
        val stepCount2 = StepCount2.Builder(stepCount, timeInterval).build()
        return OmhDTO(extUserId = fitbitUserId, date = dateTime, stepCount2 = stepCount2)
    }
}