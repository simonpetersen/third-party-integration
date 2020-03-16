package dtu.openhealth.integration.data.garmin

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
)
