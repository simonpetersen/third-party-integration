package dtu.openhealth.integration.data.garmin

import org.openmhealth.schema.domain.omh.Measure

open class RespirationSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val startTimeInSeconds: Float,
        val startTimeOffsetInSeconds: Int,
        val timeOffsetEpochToBreaths: Map<String, Float>
): GarminData() {
    override fun mapToOMH(): List<Measure> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

