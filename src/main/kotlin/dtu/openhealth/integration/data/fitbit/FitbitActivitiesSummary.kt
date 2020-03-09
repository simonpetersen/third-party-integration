package dtu.openhealth.integration.data.fitbit

import kotlinx.serialization.Serializable

@Serializable
data class FitbitActivitiesSummary(
        val activities: List<FitbitActivity>,
        val goals: FitbitActivityGoals,
        val summary: FitbitActivitySummary
) : FitbitData()

@Serializable
data class FitbitActivity(
        val activityId : Long,
        val activityParentId: Long,
        val calories: Long,
        val description: String,
        val distance: Double,
        val duration: Long,
        val hasStartTime: Boolean,
        val isFavorite : Boolean,
        val logId: Long,
        val name: String,
        val startTime: String?, // Time
        val steps: Long
)

@Serializable
data class FitbitActivityGoals(
        val caloriesOut: Long,
        val activeMinutes: Long,
        val distance: Double,
        val steps: Long,
        val floors: Long? = null
)

@Serializable
data class FitbitActivitySummary(
        val activeScore: Int?,
        val activityCalories: Long,
        val caloriesBMR: Long,
        val caloriesOut: Long,
        val fairlyActiveMinutes: Long,
        val lightlyActiveMinutes: Long,
        val marginalCalories: Long,
        val sedentaryMinutes: Long,
        val steps: Long,
        val veryActiveMinutes: Long,
        val floors: Long? = null,
        val elevation: Double? = null,
        val distances: List<FitbitActivityDistance>,
        val heartRateZones: List<FitbitHeartRateZone>
)

@Serializable
data class FitbitActivityDistance(
        val activity : String,
        val distance : Double
)

@Serializable
data class FitbitHeartRateZone(
        val caloriesOut: Double,
        val max: Long,
        val min: Long,
        val minutes: Long,
        val name: String
)