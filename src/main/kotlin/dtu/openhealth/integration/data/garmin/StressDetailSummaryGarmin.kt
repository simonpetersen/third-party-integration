package dtu.openhealth.integration.data.garmin

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
): GarminData()

