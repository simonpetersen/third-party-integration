package dtu.openhealth.integration.garmin.mapping

import dtu.openhealth.integration.garmin.garmin.GarminData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.Measure

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

    override fun mapToOMH(): List<Measure> {
        TODO("Not yet implemented")
    }


}
