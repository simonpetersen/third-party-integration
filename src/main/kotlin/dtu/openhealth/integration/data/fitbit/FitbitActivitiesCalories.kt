package dtu.openhealth.integration.data.fitbit

import dtu.openhealth.integration.common.serialization.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class FitbitActivitiesCalories(
        @SerialName("activities-calories") val calories: List<FitbitCalories>
) : FitbitData()

@Serializable
data class FitbitCalories(
        @Serializable(with = LocalDateSerializer::class) val dateTime: LocalDate,
        val value: Long
)