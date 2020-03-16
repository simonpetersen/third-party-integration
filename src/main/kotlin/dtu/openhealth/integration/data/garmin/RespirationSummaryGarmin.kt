package dtu.openhealth.integration.data.garmin

open class RespirationSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val startTimeInSeconds: Float,
        val startTimeOffsetInSeconds: Int,
        val timeOffsetEpochToBreaths: Map<String, Float>
)
