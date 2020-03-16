package dtu.openhealth.integration.data.garmin

open class MoveIQSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val calendarDate: String, //Date
        val startTimeInSeconds: Float,
        val startTimeOffsetInSeconds: Int,
        val durationInSeconds: Int,
        val activityType: String,
        val activitySubType: String
)
