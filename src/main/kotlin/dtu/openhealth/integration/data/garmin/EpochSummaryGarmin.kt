package dtu.openhealth.integration.data.garmin

data class EpochSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val startTimeInSeconds: Int,
        val startTimeOffsetInSeconds: Int,
        val activityType: String,
        val durationInSeconds: Int,
        val activeTimeInSeconds: Int,
        val steps: Int,
        val distanceInMeters: Float,
        val activeKilocalories: Int,
        val met: Float,
        val intensity: String,
        val meanMotionIntensity: Float,
        val maxMotionIntensity: Float
)
