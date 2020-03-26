package dtu.openhealth.integration.fitbit.mapping

import dtu.openhealth.integration.data.fitbit.FitbitActivitiesSummary
import dtu.openhealth.integration.data.fitbit.FitbitActivity
import dtu.openhealth.integration.data.fitbit.FitbitActivitySummary
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class FitbitActivityMappingTest {

    @Test
    fun testFitbitActivitySummaryMapping_NoActivity() {
        val activitySummary = prepareActivitySummary()
        val expectedOmhElements = 3
        val fitbitSummary = FitbitActivitiesSummary(emptyList(), summary = activitySummary)
        val omhData = fitbitSummary.mapToOMH()

        assertThat(omhData.size).isEqualTo(expectedOmhElements)

        validateHeartRate(omhData, activitySummary)
        validateCalories(omhData, activitySummary)
        validateStepCount(omhData, activitySummary)
    }

    @Test
    fun testFitbitActivitySummaryMapping_RunningActivity() {
        val activitySummary = prepareActivitySummary()
        val activity = prepareActivity()
        val expectedOmhElements = 4

        val fitbitSummary = FitbitActivitiesSummary(listOf(activity), summary = activitySummary)
        val omhData = fitbitSummary.mapToOMH()

        assertThat(omhData.size).isEqualTo(expectedOmhElements)

        validateHeartRate(omhData, activitySummary)
        validateCalories(omhData, activitySummary)
        validateStepCount(omhData, activitySummary)
        validateActivity(omhData, activity)
    }

    @Test
    fun testFitbitActivityMapping_WithDistance() {
        val activity = prepareActivityWithDistance()
        val omhData = activity.mapToOMH()

        assertThat(omhData).isNotNull

        validateActivityWithDistance(listOf(omhData), activity)
    }

    private fun validateHeartRate(omhData: List<Measure>, summary: FitbitActivitySummary) {
        val heartRateList = omhData.filterIsInstance<HeartRate>()
        val expectedElements = 1
        assertThat(heartRateList.size).isEqualTo(expectedElements)

        val heartRate = heartRateList[0]
        assertThat(heartRate.heartRate?.value?.longValueExact()).isEqualTo(summary.restingHeartRate)
        assertThat(heartRate.temporalRelationshipToPhysicalActivity).isEqualTo(TemporalRelationshipToPhysicalActivity.AT_REST)
    }

    private fun validateStepCount(omhData: List<Measure>, summary: FitbitActivitySummary) {
        val stepCountList = omhData.filterIsInstance<StepCount2>()
        val expectedElements = 1
        assertThat(stepCountList.size).isEqualTo(expectedElements)

        val stepCount = stepCountList[0]
        assertThat(stepCount.stepCount.longValueExact()).isEqualTo(summary.steps)
    }

    private fun validateCalories(omhData: List<Measure>, summary: FitbitActivitySummary) {
        val caloriesList = omhData.filterIsInstance<CaloriesBurned2>()
        val expectedElements = 1
        assertThat(caloriesList.size).isEqualTo(expectedElements)

        val caloriesBurned = caloriesList[0]
        assertThat(caloriesBurned.kcalBurned.value.longValueExact()).isEqualTo(summary.caloriesOut)
    }

    private fun validateActivity(omhData: List<Measure>, activity: FitbitActivity) {
        val activitiesList = omhData.filterIsInstance<PhysicalActivity>()
        val expectedElements = 1
        assertThat(activitiesList.size).isEqualTo(expectedElements)

        val physicalActivity = activitiesList[0]
        validatePhysicalActivity(physicalActivity, activity)
    }

    private fun validateActivityWithDistance(omhData: List<Measure>, activity: FitbitActivity) {
        val activitiesList = omhData.filterIsInstance<PhysicalActivity>()
        val expectedElements = 1
        assertThat(activitiesList.size).isEqualTo(expectedElements)

        val physicalActivity = activitiesList[0]
        assertThat(physicalActivity.distance).isNotNull
        assertThat(physicalActivity.distance?.value).isEqualTo(BigDecimal.valueOf(activity.distance!!))
        validatePhysicalActivity(physicalActivity, activity)
    }

    private fun validatePhysicalActivity(physicalActivity: PhysicalActivity, activity: FitbitActivity) {
        assertThat(physicalActivity.activityName).isEqualTo(activity.name)
        assertThat(physicalActivity.caloriesBurned.value.longValueExact()).isEqualTo(activity.calories)

        val startDateTime = LocalDateTime.of(2020,3,24,8,10).atOffset(ZoneOffset.UTC)
        assertThat(physicalActivity.effectiveTimeFrame.timeInterval.startDateTime).isEqualTo(startDateTime)
        assertThat(physicalActivity.effectiveTimeFrame.timeInterval.duration.value.longValueExact()).isEqualTo(activity.duration)
        assertThat(physicalActivity.effectiveTimeFrame.timeInterval.duration.typedUnit).isEqualTo(DurationUnit.MILLISECOND)
    }

    private fun prepareActivitySummary() : FitbitActivitySummary {
        return FitbitActivitySummary(
                activityCalories = 451,
                caloriesBMR = 1119,
                caloriesOut = 1410,
                fairlyActiveMinutes = 14,
                lightlyActiveMinutes = 49,
                marginalCalories = 207,
                sedentaryMinutes = 745,
                steps = 2611,
                veryActiveMinutes = 6,
                distances = emptyList(),
                restingHeartRate = 49
        )
    }

    private fun prepareActivity() : FitbitActivity {
        return FitbitActivity(
                activityId = 1,
                name = "Outside Running",
                calories = 345,
                description = "Running",
                hasStartTime = true,
                duration = 1200000,
                startDate = LocalDate.of(2020,3,24),
                startTime = LocalTime.of(8,10,0),
                steps = 3876
        )
    }

    private fun prepareActivityWithDistance() : FitbitActivity {
        return FitbitActivity(
                activityId = 1,
                name = "Outside Running",
                calories = 345,
                description = "Running",
                hasStartTime = true,
                duration = 1200000,
                distance = 4.1,
                startDate = LocalDate.of(2020,3,24),
                startTime = LocalTime.of(8,10,0),
                steps = 3876
        )
    }

}