package dtu.openhealth.integration.data.omh

// Replace with Java classes from https://github.com/openmhealth/schemas
open class OpenMHealthData

data class OMHCalories(
        val effective_time_frame: OMHEffectiveTimeFrame,
        val kcal_burned: OMHKcalBurned,
        val descriptive_statistic: String?,
        val activity_name: String?,
        val descriptive_statistic_denominator: String?
) : OpenMHealthData()

data class OMHKcalBurned(
        val value: Int,
        val unit: String
)

data class OMHEffectiveTimeFrame(
        val time_interval: OMHTimeInterval
)

data class OMHTimeInterval(val start_date_time: String, val end_date_time: String)