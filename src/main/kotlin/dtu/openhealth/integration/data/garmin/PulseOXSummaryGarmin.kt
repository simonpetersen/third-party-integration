package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.Measure

open class PulseOXSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val calendarDate: String,
        val startTimeInSeconds: Float,
        val startTimeOffsetInSeconds: Int,
        val durationInSeconds: Int,
        val timeOffsetSpo2Values: Map<String, Int>,
        val onDemand: Boolean
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

