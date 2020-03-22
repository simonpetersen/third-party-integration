package dtu.openhealth.integration.garmin.mapping

import dtu.openhealth.integration.data.garmin.DailySummaryGarmin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.CaloriesBurned2
import org.openmhealth.schema.domain.omh.HeartRate
import org.openmhealth.schema.domain.omh.StepCount2

class DailyMappingOMHTest {

    private val caloriesActivity = 300
    private val caloriesBMR = 1000
    private val distance = 1
    private val startTime = 1
    private val startTimeOffset = 1
    private val duration = 5
    private val steps = 100
    private val averageHeathBeats = 60

    private val dailySummaryGarminAllData = DailySummaryGarmin("4aacafe82427c251df9c9592d0c06768",
            "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", "EXAMPLE_678901", "1970-01-01",
            startTime, startTimeOffset, "WALKING", duration, steps, distance.toFloat(), 5,
            caloriesActivity, caloriesBMR, 10, 0, 0, 0,
    0, averageHeathBeats, 100, 50, null,
    50, 75, 10, 10, 10, 10,
    10, 10, "Stress", 5, 5, 5)

    @Test
    fun testAllDataFields() {
        val measures = dailySummaryGarminAllData.mapToOMH()
        assertThat(measures.size).isEqualTo(3)

        assertThat(measures[0]).isInstanceOf(StepCount2::class.java)
        val stepCount = measures[0] as StepCount2
        assertThat(stepCount.stepCount).isEqualTo(steps.toBigDecimal())
        assertThat(stepCount.effectiveTimeFrame.timeInterval.startDateTime.toEpochSecond())
                .isEqualTo((startTime - startTimeOffset).toLong())

        assertThat(measures[1]).isInstanceOf(CaloriesBurned2::class.java)
        val calories = measures[1] as CaloriesBurned2
        assertThat(calories.kcalBurned.value).isEqualTo((caloriesActivity+caloriesBMR).toBigDecimal())
        assertThat(calories.effectiveTimeFrame.timeInterval.startDateTime.toEpochSecond())
                .isEqualTo((startTime - startTimeOffset).toLong())

        assertThat(measures[2]).isInstanceOf(HeartRate::class.java)
        val heartRate = measures[2] as HeartRate
        assertThat(heartRate.heartRate.value).isEqualTo(averageHeathBeats.toBigDecimal())
        assertThat(heartRate.effectiveTimeFrame.timeInterval.startDateTime.toEpochSecond())
                .isEqualTo((startTime - startTimeOffset).toLong())
    }



}
