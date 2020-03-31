package dtu.openhealth.integration.garmin.garmin

import org.openmhealth.schema.domain.omh.*

data class BodyCompositionSummaryGarmin(
        val userId: String? = null,
        val userAccessToken: String? = null,
        val summaryId: String? = null,
        val measurementTimeInSeconds: Int? = null,
        val measurementTimeOffsetInSeconds: Int? = null,
        val muscleMassInGrams: Int? = null,
        val boneMassInGrams: Int? = null,
        val bodyWaterInPercent: Float? = null,
        val bodyFatInPercent: Float? = null,
        val bodyMassIndex: Float? = null,
        val weightInGrams: Int? = null
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        val measures = mutableListOf<Measure>()
        weightInGrams?.let {
            measures.add(BodyWeight.Builder(MassUnitValue(MassUnit.GRAM, it.toBigDecimal())).build())
        }
        bodyMassIndex?.let {
            measures.add(BodyMassIndex1.Builder(
                    TypedUnitValue(BodyMassIndexUnit1.KILOGRAMS_PER_SQUARE_METER, it.toBigDecimal()))
                    .build())
        }
        bodyFatInPercent?.let {
            measures.add(BodyFatPercentage.Builder(
                    TypedUnitValue(PercentUnit.PERCENT, it.toBigDecimal()))
                    .build())
        }
        return measures
    }
}

