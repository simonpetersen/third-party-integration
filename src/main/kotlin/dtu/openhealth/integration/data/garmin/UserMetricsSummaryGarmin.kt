package dtu.openhealth.integration.data.garmin

import dtu.openhealth.integration.common.exception.NoMappingFoundException
import org.openmhealth.schema.domain.omh.Measure

data class UserMetricsSummaryGarmin(
        val userId: String? = null,
        val userAccessToken: String? = null,
        val summaryId: String? = null,
        val calendarDate: String? = null, //Date
        val vo2Max: Float? = null,
        val fitnessAge: Int? = null
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        throw NoMappingFoundException("No mapping found for this type: ${this.javaClass}")
    }
}
