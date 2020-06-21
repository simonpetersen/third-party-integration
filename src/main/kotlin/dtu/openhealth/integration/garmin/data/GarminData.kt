package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.model.AThirdPartyData
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.DurationUnit
import org.openmhealth.schema.domain.omh.DurationUnitValue
import org.openmhealth.schema.domain.omh.TimeInterval
import java.time.*

@Serializable
abstract class GarminData: AThirdPartyData() {

    protected fun getTimeInterval(startTime: Int?, offSet: Int?, durationInSeconds: Int?): TimeInterval {
        val time = startTime?.toLong() ?: 0
        val timeOffset = offSet ?: 0
        val duration = durationInSeconds ?: 0

        return TimeInterval.ofStartDateTimeAndDuration(
                OffsetDateTime.of(Instant.ofEpochSecond(time)
                        .atZone(ZoneOffset.UTC).toLocalDateTime(), ZoneOffset.ofTotalSeconds(timeOffset)),
                DurationUnitValue((DurationUnit.SECOND), duration.toBigDecimal()))
    }

    protected fun getLocalDate(dateTimeInSeconds: Int?, offSet: Int?): LocalDate {
        val time = dateTimeInSeconds?.toLong() ?: 0
        val offset = offSet ?: 0
        val localDateTime = LocalDateTime.ofEpochSecond(time,0, ZoneOffset.ofTotalSeconds(offset))

        return localDateTime.toLocalDate()
    }
}
