package dtu.openhealth.integration.fitbit.data

import dtu.openhealth.integration.common.serialization.LocalDateSerializer
import dtu.openhealth.integration.common.serialization.LocalTimeSerializer
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*
import java.time.*

@Serializable
data class FitbitActivitiesSummary(
        val activities: List<FitbitActivity>,
        val goals: FitbitActivityGoals? = null,
        val summary: FitbitActivitySummary
) : FitbitData() {
    override fun mapToOMH(): List<Measure> {
        val omhData = activities.map { it.mapToOMH() }.toMutableList()
        omhData.addAll(summary.mapToOMH(LocalDate.now())) // Parse date of summary.

        return omhData
    }
}

@Serializable
data class FitbitActivity(
        val activityId : Long,
        val activityParentId: Long? = null,
        val activityParentName: String? = null,
        val calories: Long,
        val description: String,
        val distance: Double? = null,
        val duration: Long,
        val hasStartTime: Boolean = false,
        val isFavorite : Boolean = false,
        val logId: Long? = null,
        val name: String,
        @Serializable(with = LocalDateSerializer::class) val startDate: LocalDate,
        @Serializable(with = LocalTimeSerializer::class) val startTime: LocalTime? = null,
        val steps: Long
) {
    fun mapToOMH(): Measure {
        val startDateTime = if (hasStartTime) LocalDateTime.of(startDate, startTime) else startDate.atStartOfDay()
        val timeInterval = TimeInterval.ofStartDateTimeAndDuration(
                startDateTime.atOffset(ZoneOffset.UTC), DurationUnitValue(DurationUnit.MILLISECOND, duration))
        val activityBuilder = PhysicalActivity.Builder(name)
                .setCaloriesBurned(KcalUnitValue(KcalUnit.KILOCALORIE, calories))
                .setEffectiveTimeFrame(timeInterval)

        if (distance != null) {
            activityBuilder.setDistance(LengthUnitValue(LengthUnit.KILOMETER, distance))
        }

        return activityBuilder.build()
    }
}

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
        val activeScore: Int? = null,
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
        val heartRateZones: List<FitbitHeartRateZone>? = null,
        val restingHeartRate: Long? = null
) {
    fun mapToOMH(date: LocalDate) : List<Measure> {
        val omhData = mutableListOf<Measure>()
        val startDateTime = date.atStartOfDay().atOffset(ZoneOffset.UTC)
        val timeInterval = TimeInterval
                .ofStartDateTimeAndDuration(startDateTime, DurationUnitValue(DurationUnit.DAY,1))

        // Calories burned
        val kcalBurned = KcalUnitValue(KcalUnit.KILOCALORIE, caloriesOut)
        omhData.add(CaloriesBurned2.Builder(kcalBurned, timeInterval).build())

        // Step count
        omhData.add(StepCount2.Builder(steps, timeInterval).build())

        // Resting heartRate
        if (restingHeartRate != null) {
            val heartRate = HeartRate.Builder(TypedUnitValue(HeartRateUnit.BEATS_PER_MINUTE, restingHeartRate))
                    .setTemporalRelationshipToPhysicalActivity(TemporalRelationshipToPhysicalActivity.AT_REST)
                    .build()
            omhData.add(heartRate)
        }

        return omhData
    }
}

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
