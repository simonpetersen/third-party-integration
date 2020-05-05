package dtu.openhealth.integration.garmin.mapping.omh

import dtu.openhealth.integration.garmin.data.DailySummaryGarmin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class DailyMappingOMHTest {

    private val userId = "4aacafe82427c251df9c9592d0c06768"
    private val userAccessToken = "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2"
    private val caloriesActivity = 300
    private val caloriesBMR = 1000
    private val distance = 1
    private val startTime = 1
    private val startTimeOffset = 1
    private val duration = 5
    private val steps = 100
    private val averageHeathBeats = 60

    private val dailySummaryGarminAllData = DailySummaryGarmin(userId,
            userAccessToken, "EXAMPLE_678901", "1970-01-01",
            startTime, startTimeOffset, "WALKING", duration, steps, distance.toFloat(), 5,
            caloriesActivity, caloriesBMR, 10, 0, 0, 0,
    0, averageHeathBeats, 100, 50, null,
    50, 75, 10, 10, 10, 10,
    10, 10, "Stress", 5, 5, 5)

    @Test
    fun testAllDataFields() {
        val omhDTO = dailySummaryGarminAllData.mapToOMH()
        val localDate = LocalDateTime
                .ofEpochSecond(startTime.toLong(), 0, ZoneOffset.ofTotalSeconds(startTimeOffset))
                .toLocalDate()
        assertThat(omhDTO.extUserId).isEqualTo(userAccessToken)
        assertThat(omhDTO.date).isEqualTo(localDate)

        val stepCount = omhDTO.stepCount2
        assertThat(stepCount).isNotNull
        assertThat(stepCount?.stepCount).isEqualTo(steps.toBigDecimal())
        assertThat(stepCount?.effectiveTimeFrame?.timeInterval?.startDateTime?.toEpochSecond())
                .isEqualTo((startTime - startTimeOffset).toLong())

        val calories = omhDTO.caloriesBurned2
        assertThat(calories?.kcalBurned?.value).isEqualTo((caloriesActivity+caloriesBMR).toBigDecimal())
        assertThat(calories?.effectiveTimeFrame?.timeInterval?.startDateTime?.toEpochSecond())
                .isEqualTo((startTime - startTimeOffset).toLong())

        val heartRate = omhDTO.heartRate
        assertThat(heartRate).isNotNull
        assertThat(heartRate?.heartRate?.value).isEqualTo(averageHeathBeats.toBigDecimal())
        assertThat(heartRate?.effectiveTimeFrame?.timeInterval?.startDateTime?.toEpochSecond())
                .isEqualTo((startTime - startTimeOffset).toLong())
    }



}
