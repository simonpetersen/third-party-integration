package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.Measure

data class UserMetricsSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val calendarDate: String, //Date
        val vo2Max: Float,
        val fitnessAge: Int
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
