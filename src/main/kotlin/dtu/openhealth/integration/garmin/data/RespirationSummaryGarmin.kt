package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.RespiratoryRate
import org.openmhealth.schema.domain.omh.TypedUnitValue
import java.math.BigDecimal

@Serializable
data class RespirationSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val startTimeInSeconds: Float? = null,
        val durationInSeconds: Int? = null,
        val startTimeOffsetInSeconds: Int? = null,
        val timeOffsetEpochToBreaths: Map<String, Float>? = null
): GarminData() {
    override fun mapToOMH(parameters: Map<String,String>): OmhDTO {
        val respiratoryRate = timeOffsetEpochToBreaths?.let {
            RespiratoryRate.Builder(
                    TypedUnitValue((RespiratoryRate.RespirationUnit.BREATHS_PER_MINUTE), averageBreathsPrMinute(it)))
                    .setEffectiveTimeFrame(getTimeInterval(startTimeInSeconds?.toInt(), startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        val localDate = getLocalDate(startTimeInSeconds?.toInt(), startTimeOffsetInSeconds)

        return OmhDTO(extUserId = userAccessToken, date = localDate, respiratoryRate = respiratoryRate)
    }

    private fun averageBreathsPrMinute(breaths: Map<String, Float>): BigDecimal {
        return breaths.values.average().toBigDecimal()
    }
}

