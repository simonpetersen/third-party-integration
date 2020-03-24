package dtu.openhealth.integration.fitbit.mapping

import dtu.openhealth.integration.data.fitbit.FitbitActivitiesSummary
import dtu.openhealth.integration.data.fitbit.FitbitActivity
import dtu.openhealth.integration.data.fitbit.FitbitActivitySummary
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class FitbitActivityMappingTest {

    @Test
    fun testFitbitActivitySummaryMapping_NoActivity() {
        val activitySummary = prepareActivitySummary()
        val expectedOmhElements = 3
        val fitbitSummary = FitbitActivitiesSummary(emptyList(), summary = activitySummary)
        val omhData = fitbitSummary.mapToOMH()

        assertThat(omhData.size).isEqualTo(expectedOmhElements)
    }

    @Test
    fun testFitbitActivitySummaryMapping_RunningActivity() {
        val activitySummary = prepareActivitySummary()
        val activity = prepareActivity()
        val expectedOmhElements = 4

        val fitbitSummary = FitbitActivitiesSummary(listOf(activity), summary = activitySummary)
        val omhData = fitbitSummary.mapToOMH()

        assertThat(omhData.size).isEqualTo(expectedOmhElements)
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

}