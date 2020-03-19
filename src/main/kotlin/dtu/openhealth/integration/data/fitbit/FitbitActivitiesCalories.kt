package dtu.openhealth.integration.data.fitbit

import dtu.openhealth.integration.common.serialization.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.Measure
import java.time.LocalDate

@Serializable
data class FitbitActivitiesCalories(
        @SerialName("activities-calories") val calories: List<FitbitCalories>
) : FitbitData() {
    override fun mapToOMH(): List<Measure> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

@Serializable
data class FitbitCalories(
        @Serializable(with = LocalDateSerializer::class) val dateTime: LocalDate,
        val value: Long
)
