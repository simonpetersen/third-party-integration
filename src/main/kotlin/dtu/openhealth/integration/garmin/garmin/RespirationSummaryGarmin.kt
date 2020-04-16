package dtu.openhealth.integration.garmin.garmin

import dtu.openhealth.integration.shared.dto.OmhDTO
import org.openmhealth.schema.domain.omh.RespiratoryRate
import org.openmhealth.schema.domain.omh.TypedUnitValue
import java.math.BigDecimal

data class RespirationSummaryGarmin(
        val userId: String? = null,
        val userAccessToken: String? = null,
        val summaryId: String? = null,
        val startTimeInSeconds: Float? = null,
        val durationInSeconds: Int? = null,
        val startTimeOffsetInSeconds: Int? = null,
        val timeOffsetEpochToBreaths: Map<String, Float>? = null
): GarminData() {
    override fun mapToOMH(): OmhDTO {
        val respiratoryRate = timeOffsetEpochToBreaths?.let {
            RespiratoryRate.Builder(
                    TypedUnitValue((RespiratoryRate.RespirationUnit.BREATHS_PER_MINUTE), averageBreathsPrMinute(it)))
                    .setEffectiveTimeFrame(getTimeInterval(startTimeInSeconds?.toInt(), startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        return OmhDTO(userId = userId, respiratoryRate = respiratoryRate)
    }

    private fun averageBreathsPrMinute(breaths: Map<String, Float>): BigDecimal {
        return breaths.values.average().toBigDecimal()
    }
}

