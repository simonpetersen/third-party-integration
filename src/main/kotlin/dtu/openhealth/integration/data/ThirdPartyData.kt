package dtu.openhealth.integration.data

import dtu.openhealth.integration.common.serialization.DateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.util.*

@Serializable
open class ThirdPartyData

@Serializable
open class FitbitData : ThirdPartyData()

@Serializable
data class FitbitActivitiesCalories(
        @SerialName("activities-calories") val calories: List<FitbitCalories>
) : FitbitData()

@Serializable
data class FitbitCalories(
        @Serializable(with = DateSerializer::class) val dateTime: Date,
        val value: Long
)

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
        val distance: Double,
        val floors: Long,
        val steps: Long
)

@Serializable
data class FitbitActivitySummary(
        val activityCalories: Long,
        val caloriesBMR: Long,
        val caloriesOut: Long,
        val fairlyActiveMinutes: Long,
        val lightlyActiveMinutes: Long,
        val marginalCalories: Long,
        val sedentaryMinutes: Long,
        val steps: Long,
        val veryActiveMinutes: Long,
        val elevation: Double
)

@Serializable
data class FitbitActivityDistance(
        val activity : String,
        val distance : Double
)