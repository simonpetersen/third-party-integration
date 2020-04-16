package dtu.openhealth.integration.fitbit.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.serialization.LocalDateSerializer
import dtu.openhealth.integration.shared.util.serialization.LocalTimeSerializer
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*
import java.time.*

@Serializable
data class FitbitActivitiesSummary(
        val activities: List<FitbitActivity>,
        val goals: FitbitActivityGoals? = null,
        val summary: FitbitActivitySummary
) : FitbitData() {
    override fun mapToOMH(): OmhDTO {
        val omhDTO = summary.mapToOMH(LocalDate.now()) // Parse date of summary.

        val activityList = activities.map { it.mapToOMH() }.toMutableList()
        omhDTO.physicalActivities = activityList

        return omhDTO
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
    fun mapToOMH(): PhysicalActivity {
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
    fun mapToOMH(date: LocalDate) : OmhDTO {
        val startDateTime = date.atStartOfDay().atOffset(ZoneOffset.UTC)
        val timeInterval = TimeInterval
                .ofStartDateTimeAndDuration(startDateTime, DurationUnitValue(DurationUnit.DAY,1))

        // Calories burned
        val kcalBurned = KcalUnitValue(KcalUnit.KILOCALORIE, caloriesOut)
        val caloriesBurned2 = CaloriesBurned2.Builder(kcalBurned, timeInterval).build()

        // Step count
        val stepCount2 = StepCount2.Builder(steps, timeInterval).build()

        // Resting heartRate
        var heartRate: HeartRate? = null
        if (restingHeartRate != null) {
            heartRate = HeartRate.Builder(TypedUnitValue(HeartRateUnit.BEATS_PER_MINUTE, restingHeartRate))
                    .setTemporalRelationshipToPhysicalActivity(TemporalRelationshipToPhysicalActivity.AT_REST)
                    .build()
        }

        return OmhDTO(caloriesBurned2 = listOf(caloriesBurned2), heartRate = heartRate, stepCount2 = stepCount2)
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
