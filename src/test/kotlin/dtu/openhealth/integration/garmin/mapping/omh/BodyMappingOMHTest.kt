package dtu.openhealth.integration.garmin.mapping.omh

import dtu.openhealth.integration.garmin.garmin.BodyCompositionSummaryGarmin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.*

class BodyMappingOMHTest {

    private val weight = 1000000 // grams
    private val bmi = 20
    private val bodyFat = 15

    private val bodySummaryDataAllFields = BodyCompositionSummaryGarmin("4aacafe82427c251df9c9592d0c06768",
            "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", "EXAMPLE_678901", 1439741130,
            0, 25478, 2437, 59.4.toFloat(),
            bodyFat.toFloat(), bmi.toFloat(), weight)

    private val bodySummaryData = BodyCompositionSummaryGarmin("4aacafe82427c251df9c9592d0c06768",
            "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", "EXAMPLE_678901", 1439741130,
            0, 25478, 2437, 59.4.toFloat(), null, null,
            weight)

    private val bodySummaryDataNoOMH = BodyCompositionSummaryGarmin("4aacafe82427c251df9c9592d0c06768",
            "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", "EXAMPLE_678901", 1439741130,
            0, 25478, 2437, 59.4.toFloat(), null, null,
            null)


    @Test
    fun testMappingToOMH() {
        val omhDTO = bodySummaryDataAllFields.mapToOMH()

        assertThat(omhDTO.bodyWeight).isNotNull
        val bodyWeight = omhDTO.bodyWeight
        assertThat(bodyWeight?.bodyWeight).isEqualTo(MassUnitValue(MassUnit.GRAM, weight.toBigDecimal()))

        assertThat(omhDTO.bodyMassIndex1).isNotNull
        val bodyMassIndex = omhDTO.bodyMassIndex1
        assertThat(bodyMassIndex?.bodyMassIndex).isEqualTo(TypedUnitValue(BodyMassIndexUnit1.KILOGRAMS_PER_SQUARE_METER, bmi.toBigDecimal()))

        assertThat(omhDTO.bodyFatPercentage).isNotNull
        val bodyFatPercentage = omhDTO.bodyFatPercentage
        assertThat(bodyFatPercentage?.bodyFatPercentage).isEqualTo(TypedUnitValue(PercentUnit.PERCENT, bodyFat.toBigDecimal()))
    }

    @Test
    fun testNonNullMapping() {
        val omhDTO = bodySummaryData.mapToOMH()

        assertThat(omhDTO.bodyWeight).isNotNull
        val bodyWeight = omhDTO.bodyWeight
        assertThat(bodyWeight?.bodyWeight).isEqualTo(MassUnitValue(MassUnit.GRAM, weight.toBigDecimal()))

        assertThat(omhDTO.bodyMassIndex1).isNull()
        assertThat(omhDTO.bodyFatPercentage).isNull()
    }

    @Test
    fun emptyList() {
        val omhDTO = bodySummaryDataNoOMH.mapToOMH()
        assertThat(omhDTO.bodyWeight).isNull()
        assertThat(omhDTO.bodyMassIndex1).isNull()
        assertThat(omhDTO.bodyFatPercentage).isNull()
    }

}
