package dtu.openhealth.integration.garmin.mapping

import dtu.openhealth.integration.data.garmin.BodyCompositionSummaryGarmin
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
        val measures = bodySummaryDataAllFields.mapToOMH()
        assertThat(measures.size).isEqualTo(3)

        assertThat(measures[0]).isInstanceOf(BodyWeight::class.java)
        val bodyWeight = measures[0] as BodyWeight
        assertThat(bodyWeight.bodyWeight).isEqualTo(MassUnitValue(MassUnit.GRAM, weight.toBigDecimal()))

        assertThat(measures[1]).isInstanceOf(BodyMassIndex1::class.java)
        val bodyMassIndex = measures[1] as BodyMassIndex1
        assertThat(bodyMassIndex.bodyMassIndex).isEqualTo(TypedUnitValue(BodyMassIndexUnit1.KILOGRAMS_PER_SQUARE_METER, bmi.toBigDecimal()))

        assertThat(measures[2]).isInstanceOf(BodyFatPercentage::class.java)
        val bodyFatPercentage = measures[2] as BodyFatPercentage
        assertThat(bodyFatPercentage.bodyFatPercentage).isEqualTo(TypedUnitValue(PercentUnit.PERCENT, bodyFat.toBigDecimal()))
    }

    @Test
    fun testNonNullMapping() {
        val measures = bodySummaryData.mapToOMH()
        assertThat(measures.size).isEqualTo(1)
        assertThat(measures[0]).isInstanceOf(BodyWeight::class.java)

        val bodyWeight = measures[0] as BodyWeight
        assertThat(bodyWeight.bodyWeight).isEqualTo(MassUnitValue(MassUnit.GRAM, weight.toBigDecimal()))
    }

    @Test
    fun emptyList() {
        val measures = bodySummaryDataNoOMH.mapToOMH()
        assertThat(measures.size).isEqualTo(0)
    }

}
