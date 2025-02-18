package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GarminDataTest: GarminData() {

    private val startTimeInSeconds = 1452470400
    private val duration = 86400
    private val offset = 3600

    @Test
    fun testGetTimeInterval() {
        val timeInterval = super.getTimeInterval(startTimeInSeconds, offset, duration)
        assertThat(timeInterval.duration.value).isEqualTo(duration.toBigDecimal())
        assertThat(timeInterval.startDateTime.toEpochSecond().toInt()).isEqualTo(startTimeInSeconds - offset)
        assertThat(timeInterval.startDateTime.offset.totalSeconds).isEqualTo(offset)
    }

    override fun mapToOMH(parameters: Map<String,String>): OmhDTO {
        TODO("Not yet implemented")
    }
}
