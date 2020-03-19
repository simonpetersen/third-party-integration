package dtu.openhealth.integration.data.garmin

import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Serializable
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

