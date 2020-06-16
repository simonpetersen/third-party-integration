package dtu.openhealth.integration.fitbit.data.weight

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.fitbit.data.FitbitData
import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*
import java.time.LocalDate

@Serializable
data class FitbitWeightLog(
        val weight: List<FitbitWeight>
): FitbitData() {

    override fun mapToOMH(parameters: Map<String, String>): OmhDTO
    {
        val fitbitUserId = parameters[FitbitConstants.UserParameterTag]
        val dateParameter = parameters[FitbitConstants.DateParameterTag]
        val date = if (dateParameter != null) LocalDate.parse(dateParameter) else LocalDate.now()

        return weight
                .map { it.mapToOMH(fitbitUserId, date) }
                .first()
    }

}

@Serializable
data class FitbitWeight(
        val bmi: Double,
        @Serializable(with = LocalDateSerializer::class) val date: LocalDate,
        val logId: Long,
        val time: String,
        val weight: Double,
        val source: String
) {

    fun mapToOMH(fitbitUserId: String?, date: LocalDate): OmhDTO
    {
        val bodyWeight = BodyWeight.Builder(MassUnitValue(MassUnit.KILOGRAM, weight)).build()
        val bodyMassIndex = BodyMassIndex1.Builder(
                    TypedUnitValue(BodyMassIndexUnit1.KILOGRAMS_PER_SQUARE_METER, bmi)
        ).build()

        return OmhDTO(extUserId = fitbitUserId, date = date, bodyWeight = bodyWeight, bodyMassIndex1 = bodyMassIndex)
    }
}