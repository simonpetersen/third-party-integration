package dtu.openhealth.integration.data.garmin

import dtu.openhealth.integration.data.ThirdPartyData
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.DurationUnit
import org.openmhealth.schema.domain.omh.DurationUnitValue
import org.openmhealth.schema.domain.omh.TimeInterval
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Serializable
abstract class GarminData: ThirdPartyData() {
    /*
        Add garmin specific methods (if any)
     */
    protected fun getTimeInterval(startTime: Int?, offSet: Int?, durationInSeconds: Int?): TimeInterval {
        val start = startTime?.plus(offSet!!)?.toLong();
        return TimeInterval.ofStartDateTimeAndDuration(
                start?.let { Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toOffsetDateTime() },
                DurationUnitValue((DurationUnit.SECOND), durationInSeconds?.toBigDecimal()))
    }
}
