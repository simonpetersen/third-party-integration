package dtu.openhealth.integration.garmin.data

import dtu.openhealth.integration.shared.model.ThirdPartyData
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.DurationUnit
import org.openmhealth.schema.domain.omh.DurationUnitValue
import org.openmhealth.schema.domain.omh.TimeInterval
import java.time.*

@Serializable
abstract class GarminData: ThirdPartyData() {

    protected fun getTimeInterval(startTime: Int?, offSet: Int?, durationInSeconds: Int?): TimeInterval {
        return TimeInterval.ofStartDateTimeAndDuration(
                startTime?.let { OffsetDateTime.of(Instant.ofEpochSecond(it.toLong())
                        .atZone(ZoneOffset.UTC).toLocalDateTime(), ZoneOffset.ofTotalSeconds(offSet!!)) },
                DurationUnitValue((DurationUnit.SECOND), durationInSeconds?.toBigDecimal()))
    }

    protected fun getLocalDate(dateTimeInSeconds: Int?, offSet: Int?): LocalDate {
        val time = dateTimeInSeconds?.toLong() ?: 0
        val offset = offSet ?: 0
        val localDateTime = LocalDateTime.ofEpochSecond(time,0, ZoneOffset.ofTotalSeconds(offset))

        return localDateTime.toLocalDate()
    }
}
