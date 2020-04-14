package dtu.openhealth.integration.fitbit.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.serialization.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
data class FitbitActivitiesCalories(
        @SerialName("activities-calories") val calories: List<FitbitCalories>
) : FitbitData() {
    override fun mapToOMH(): List<OmhDTO>  {
        return calories.map { it.mapToOMH() }
    }
}

@Serializable
data class FitbitCalories(
        @Serializable(with = LocalDateSerializer::class) val dateTime: LocalDate,
        val value: Long
) {
    fun mapToOMH(): OmhDTO {
        val startDateTime = OffsetDateTime.of(dateTime, LocalTime.MIDNIGHT, ZoneOffset.UTC)
        val timeInterval = TimeInterval
                .ofStartDateTimeAndDuration(startDateTime, DurationUnitValue(DurationUnit.DAY,1))
        val caloriesBurned2 = CaloriesBurned2
                .Builder(KcalUnitValue(KcalUnit.KILOCALORIE, value), timeInterval).build()
        return OmhDTO(caloriesBurned2 = caloriesBurned2)
    }
}
