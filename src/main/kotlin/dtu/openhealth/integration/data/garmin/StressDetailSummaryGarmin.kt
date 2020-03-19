package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.Measure

data class StressDetailSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val startTimeInSeconds: Int,
        val startTimeOffsetInSeconds: Int,
        val durationInSeconds: Int,
        val calendarDate: String, //Date
        val timeOffsetStressLevelValues: Map<String, Int>,
        val timeOffsetBodyBatteryDetails: Map<String, Int>
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

