package dtu.openhealth.integration.fitbit.data.activities

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.shared.dto.OmhDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class FitbitActivityMappingTest {
    private val activityDate = LocalDate.of(2020,6,27)
    private val activityDateString = "2020-06-27"
    private val fitbitUserId = "hjdlafhska"
    private val durationUnitValue = DurationUnitValue(DurationUnit.DAY,1)
    private val expectedStartDateTime = activityDate.atStartOfDay().atOffset(ZoneOffset.UTC)

    @Test
    fun testFitbitActivitySummaryMapping_NoActivity() {
        val activitySummary = prepareActivitySummary()
        val activityGoals = prepareActivityGoals()
        val fitbitSummary = FitbitActivitiesSummary(emptyList(), activityGoals, activitySummary)
        val parameters = mapOf(Pair(FitbitConstants.UserParameterTag, fitbitUserId),
                Pair(FitbitConstants.DateParameterTag, activityDateString))
        val omhDTO = fitbitSummary.mapToOMH(parameters)
        assertThat(omhDTO.extUserId).isEqualTo(fitbitUserId)
        assertThat(omhDTO.date).isEqualTo(activityDate)

        validateHeartRate(omhDTO, activitySummary)
        validateCalories(omhDTO, activitySummary)
        validateStepCount(omhDTO, activitySummary)
    }

    @Test
    fun testFitbitActivitySummaryMapping_RunningActivity() {
        val activitySummary = prepareActivitySummary()
        val activityGoals = prepareActivityGoals()
        val activity = prepareActivity()

        val fitbitSummary = FitbitActivitiesSummary(listOf(activity), activityGoals, activitySummary)
        val parameters = mapOf(Pair(FitbitConstants.UserParameterTag, fitbitUserId),
                Pair(FitbitConstants.DateParameterTag, activityDateString))
        val omhDTO = fitbitSummary.mapToOMH(parameters)
        assertThat(omhDTO.extUserId).isEqualTo(fitbitUserId)
        assertThat(omhDTO.date).isEqualTo(activityDate)

        validateHeartRate(omhDTO, activitySummary)
        validateCalories(omhDTO, activitySummary)
        validateStepCount(omhDTO, activitySummary)
        validateActivity(omhDTO, activity)
    }

    @Test
    fun testFitbitActivityMapping_WithDistance() {
        val activity = prepareActivityWithDistance()
        val physicalActivity = activity.mapToOMH()
        assertThat(physicalActivity).isNotNull
        validateActivityWithDistance(physicalActivity, activity)
    }

    private fun validateHeartRate(omhDTO: OmhDTO, summary: FitbitActivitySummary) {
        val heartRate = omhDTO.heartRate
        assertThat(heartRate).isNotNull
        assertThat(heartRate?.heartRate?.value?.longValueExact()).isEqualTo(summary.restingHeartRate)
        assertThat(heartRate?.temporalRelationshipToPhysicalActivity).isEqualTo(TemporalRelationshipToPhysicalActivity.AT_REST)
    }

    private fun validateStepCount(omhDTO: OmhDTO, summary: FitbitActivitySummary) {
        val stepCount = omhDTO.stepCount2
        assertThat(stepCount).isNotNull
        assertThat(stepCount?.stepCount?.longValueExact()).isEqualTo(summary.steps)
        assertThat(stepCount?.effectiveTimeFrame?.timeInterval?.startDateTime).isEqualTo(expectedStartDateTime)
        assertThat(stepCount?.effectiveTimeFrame?.timeInterval?.duration).isEqualTo(durationUnitValue)
    }

    private fun validateCalories(omhDTO: OmhDTO, summary: FitbitActivitySummary) {
        val caloriesBurned = omhDTO.caloriesBurned2
        assertThat(caloriesBurned?.kcalBurned?.value?.longValueExact()).isEqualTo(summary.caloriesOut)
        assertThat(caloriesBurned?.effectiveTimeFrame?.timeInterval?.startDateTime).isEqualTo(expectedStartDateTime)
        assertThat(caloriesBurned?.effectiveTimeFrame?.timeInterval?.duration).isEqualTo(durationUnitValue)
    }

    private fun validateActivity(omhDTO: OmhDTO, activity: FitbitActivity) {
        val activitiesList = omhDTO.physicalActivities
        val expectedElements = 1
        assertThat(activitiesList?.size).isEqualTo(expectedElements)

        val physicalActivity = activitiesList?.get(0)
        validatePhysicalActivity(physicalActivity!!, activity)
    }

    private fun validateActivityWithDistance(physicalActivity: PhysicalActivity, activity: FitbitActivity) {
        assertThat(physicalActivity.distance).isNotNull
        assertThat(physicalActivity.distance?.value).isEqualTo(BigDecimal.valueOf(activity.distance!!))
        validatePhysicalActivity(physicalActivity, activity)
    }

    private fun validatePhysicalActivity(physicalActivity: PhysicalActivity, activity: FitbitActivity) {
        assertThat(physicalActivity.activityName).isEqualTo(activity.name)
        assertThat(physicalActivity.caloriesBurned.value.longValueExact()).isEqualTo(activity.calories)

        val startDateTime = LocalDateTime.of(activityDate,LocalTime.of(8,10)).atOffset(ZoneOffset.UTC)
        assertThat(physicalActivity.effectiveTimeFrame.timeInterval.startDateTime).isEqualTo(startDateTime)
        assertThat(physicalActivity.effectiveTimeFrame.timeInterval.duration.value.longValueExact()).isEqualTo(activity.duration)
        assertThat(physicalActivity.effectiveTimeFrame.timeInterval.duration.typedUnit).isEqualTo(DurationUnit.MILLISECOND)
    }

    private fun prepareActivitySummary() : FitbitActivitySummary
    {
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
                distances = activityDistanceList(),
                heartRateZones = heartRateZoneList(),
                restingHeartRate = 49
        )
    }

    private fun heartRateZoneList(): List<FitbitHeartRateZone>
    {
        return listOf(FitbitHeartRateZone(345.34, 145, 125, 16, "running"))
    }

    private fun activityDistanceList(): List<FitbitActivityDistance>
    {
        return listOf(FitbitActivityDistance("running", 4.5))
    }

    private fun prepareActivity() : FitbitActivity
    {
        return FitbitActivity(
                activityId = 1,
                name = "Outside Running",
                calories = 345,
                description = "Running",
                hasStartTime = true,
                duration = 1200000,
                startDate = activityDate,
                startTime = LocalTime.of(8, 10, 0),
                steps = 3876
        )
    }

    private fun prepareActivityWithDistance() : FitbitActivity
    {
        return FitbitActivity(
                activityId = 1,
                name = "Outside Running",
                calories = 345,
                description = "Running",
                hasStartTime = true,
                duration = 1200000,
                distance = 4.1,
                startDate = activityDate,
                startTime = LocalTime.of(8, 10, 0),
                steps = 3876
        )
    }

    private fun prepareActivityGoals(): FitbitActivityGoals
    {
        return FitbitActivityGoals(
                2300,
                24,
                6.7,
                9000,
                11
        )
    }

}
