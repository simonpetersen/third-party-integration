package dtu.openhealth.integration.garmin.mapping.omh

import dtu.openhealth.integration.garmin.garmin.RespirationSummaryGarmin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.RespiratoryRate

class RespirationMappingOMHTest {

    private val startTime = 1568171700F
    private val startTimeOffset = -18000
    private val duration = 900
    private val breathList = listOf(14.63F, 14.4F, 14.38F, 14.38F, 17.1F, 16.61F, 16.14F, 14.59F, 14.65F, 15.09F, 14.88F)
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

    val respirationData = RespirationSummaryGarmin("4aacafe82427c251df9c9592d0c06768",
            "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", "EXAMPLE_678901", startTime, duration,
            startTimeOffset, breathsMap)

    @Test
    fun testMapping() {
        val measures = respirationData.mapToOMH()
        assertThat(measures[0]).isInstanceOf(RespiratoryRate::class.java)

        val respiratoryRate: RespiratoryRate = measures[0] as RespiratoryRate

        assertThat(respiratoryRate.respiratoryRate.value).isEqualTo(breathList.average().toBigDecimal())
        assertThat(respiratoryRate.effectiveTimeFrame.timeInterval.startDateTime.toEpochSecond())
                .isEqualTo((startTime.toInt() - startTimeOffset).toLong())
    }

}
