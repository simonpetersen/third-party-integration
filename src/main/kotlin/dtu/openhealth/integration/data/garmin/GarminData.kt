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
        return TimeInterval.ofStartDateTimeAndDuration(
                startTime?.let { OffsetDateTime.of(Instant.ofEpochSecond(it.toLong())
                        .atZone(ZoneOffset.UTC).toLocalDateTime(), ZoneOffset.ofTotalSeconds(offSet!!)) },
                DurationUnitValue((DurationUnit.SECOND), durationInSeconds?.toBigDecimal()))
    }
}
