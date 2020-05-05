package dtu.openhealth.integration.garmin.mapping.omh

import dtu.openhealth.integration.garmin.data.PulseOXSummaryGarmin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class PulseMappingOMHTest {

    private val garminUserId = "4aacafe82427c251df9c9592d0c06768"
    private val userAccessToken = "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2"
    private val startTime = 1568171700F
    private val startTimeOffset = -18000
    private val duration = 900
    private val breathList = listOf(14, 14, 14, 14, 17, 16, 16, 14, 14, 15, 14)
    private val breathsMap = mapOf(
            "0" to breathList[0],
            "60" to breathList[1],
            "120" to breathList[2],
            "180" to breathList[3],
            "300" to breathList[4],
            "540" to breathList[5],
            "600" to breathList[6],
            "660" to breathList[7],
            "720" to breathList[8],
            "780" to breathList[9],
            "840" to breathList[10])

    private val pulseSummary = PulseOXSummaryGarmin(garminUserId, userAccessToken,
            "EXAMPLE_678901", "1970-01-01", startTime,
            startTimeOffset, duration, breathsMap, false)

    @Test
    fun testMappingToOMH() {
        val omhDTO = pulseSummary.mapToOMH()
        val localDate = LocalDateTime
                .ofEpochSecond(startTime.toLong(), 0, ZoneOffset.ofTotalSeconds(startTimeOffset))
                .toLocalDate()
        assertThat(omhDTO.extUserId).isEqualTo(userAccessToken)
        assertThat(omhDTO.date).isEqualTo(localDate)

        val heartRate = omhDTO.heartRate
        assertThat(heartRate).isNotNull
        assertThat(heartRate?.heartRate?.value).isEqualTo(breathList.average().toBigDecimal())
        assertThat(heartRate?.effectiveTimeFrame?.timeInterval?.startDateTime?.toEpochSecond())
                .isEqualTo((startTime.toInt() - startTimeOffset).toLong())
    }

}
