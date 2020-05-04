package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import org.openmhealth.schema.domain.omh.*
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset

@Serializable
data class BodyCompositionSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val measurementTimeInSeconds: Int? = null,
        val measurementTimeOffsetInSeconds: Int? = null,
        val muscleMassInGrams: Int? = null,
        val boneMassInGrams: Int? = null,
        val bodyWaterInPercent: Float? = null,
        val bodyFatInPercent: Float? = null,
        val bodyMassIndex: Float? = null,
        val weightInGrams: Int? = null
): GarminData() {
    override fun mapToOMH(): OmhDTO {
        val bodyWeight = weightInGrams?.let {
            BodyWeight.Builder(MassUnitValue(MassUnit.GRAM, it.toBigDecimal())).build()
        }

        val bodyMassIndex = bodyMassIndex?.let {
            BodyMassIndex1.Builder(
                    TypedUnitValue(BodyMassIndexUnit1.KILOGRAMS_PER_SQUARE_METER, it.toBigDecimal())).build()
        }

        val bodyFatPercentage = bodyFatInPercent?.let {
            BodyFatPercentage.Builder(TypedUnitValue(PercentUnit.PERCENT, it.toBigDecimal())).build()
        }

        val localDate = getLocalDate(measurementTimeInSeconds, measurementTimeOffsetInSeconds)

        return OmhDTO(extUserId = userAccessToken, date = localDate, bodyWeight = bodyWeight,
                bodyMassIndex1 = bodyMassIndex, bodyFatPercentage = bodyFatPercentage)
    }
}

