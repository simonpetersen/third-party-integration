package dtu.openhealth.integration.fitbit.data.weight

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.BodyMassIndexUnit1
import org.openmhealth.schema.domain.omh.MassUnit
import java.time.LocalDate

class FitbitWeightMappingTest {

    private val fitbitUserId = "hjdlafhska"
    private val weightDate = LocalDate.of(2020,6,27)
    private val weightDateString = weightDate.toString()
    private val bmi = 15.5
    private val weight = 77.7

    @Test
    fun testFitbitWeightMapping()
    {
        val fitbitWeightLog = FitbitWeightLog(weightList())
        val parameters = urlParameters()
        val omhDTO = fitbitWeightLog.mapToOMH(parameters)

        assertThat(omhDTO.extUserId).isEqualTo(fitbitUserId)
        assertThat(omhDTO.date).isEqualTo(weightDate)

        val bodyWeight = omhDTO.bodyWeight
        assertThat(bodyWeight).isNotNull
        assertThat(bodyWeight?.bodyWeight?.value?.toDouble()).isEqualTo(weight)
        assertThat(bodyWeight?.bodyWeight?.typedUnit).isEqualTo(MassUnit.KILOGRAM)

        val bodyMassIndex = omhDTO.bodyMassIndex1
        assertThat(bodyMassIndex).isNotNull
        assertThat(bodyMassIndex?.bodyMassIndex?.value?.toDouble()).isEqualTo(bmi)
        assertThat(bodyMassIndex?.bodyMassIndex?.typedUnit).isEqualTo(BodyMassIndexUnit1.KILOGRAMS_PER_SQUARE_METER)
    }

    private fun weightList(): List<FitbitWeight>
    {
        return listOf(
                FitbitWeight(
                        bmi = bmi,
                        date = weightDate,
                        logId = 404,
                        time = "23:59:59",
                        weight = weight,
                        source = "Scale"
                )
        )
    }

    private fun urlParameters(): Map<String, String>
    {
        return mapOf(
                Pair(FitbitConstants.UserParameterTag, fitbitUserId),
                Pair(FitbitConstants.DateParameterTag, weightDateString)
        )
    }
}