package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.Measure

data class MoveIQSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val calendarDate: String, //Date
        val startTimeInSeconds: Float,
        val startTimeOffsetInSeconds: Int,
        val durationInSeconds: Int,
        val activityType: String,
        val activitySubType: String
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

