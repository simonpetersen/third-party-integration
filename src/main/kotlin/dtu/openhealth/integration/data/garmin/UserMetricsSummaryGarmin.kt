package dtu.openhealth.integration.data.garmin

data class UserMetricsSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val calendarDate: String, //Date
        val vo2Max: Float,
        val fitnessAge: Int
): GarminData()
