package dtu.openhealth.integration.fitbit.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.serialization.LocalDateSerializer
import dtu.openhealth.integration.shared.util.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*
import java.time.*

@Serializable
data class FitbitSleepLogSummary(
        val sleep : List<FitbitSleepLog>,
        val summary : FitbitSleepSummary
) : FitbitData(){
    override fun mapToOMH(): List<OmhDTO> {
        val sleepData = sleep.flatMap { it.mapToOMH() }.toMutableList()
        sleepData.addAll(summary.mapToOMH(LocalDate.now())) // TODO: Parse date in a smarter way.

        return sleepData
    }
}

@Serializable
data class FitbitSleepLog(
        val awakeCount: Long,
        val awakeDuration: Long,
        val awakeningsCount: Int,
        @Serializable(with = LocalDateSerializer::class) val dateOfSleep: LocalDate,
        val duration: Long,
        val efficiency: Long,
        @Serializable(with = LocalDateTimeSerializer::class) val endTime: LocalDateTime,
        val isMainSleep: Boolean,
        val logId: Long,
        val minuteData: List<FitbitSleepMinuteData>,
        val minutesAfterWakeup: Long,
        val minutesAsleep: Long,
        val minutesAwake: Long,
        val minutesToFallAsleep: Long,
        val restlessCount: Long,
        val restlessDuration: Long,
        @Serializable(with = LocalDateTimeSerializer::class) val startTime: LocalDateTime,
        val timeInBed: Long
) {
    fun mapToOMH(): List<OmhDTO> {
        val timeInterval = TimeInterval.ofStartDateTimeAndEndDateTime(
                startTime.atOffset(ZoneOffset.UTC), endTime.atOffset(ZoneOffset.UTC))
        val effectiveTimeFrame = TimeFrame(timeInterval)
        val sleepEpisode = SleepEpisode.Builder(effectiveTimeFrame)
                .setMainSleep(isMainSleep)
                .setNumberOfAwakenings(awakeningsCount)
                .setLatencyToSleepOnset(DurationUnitValue(DurationUnit.MINUTE, minutesToFallAsleep))
                .setTotalSleepTime(DurationUnitValue(DurationUnit.MINUTE, minutesAsleep))
                .setSleepMaintenanceEfficiencyPercentage(TypedUnitValue(PercentUnit.PERCENT, efficiency))
                .setLatencyToArising(DurationUnitValue(DurationUnit.MINUTE, minutesAfterWakeup))
                .build()

        return listOf(OmhDTO(sleepEpisode = sleepEpisode))
    }
}

@Serializable
data class FitbitSleepMinuteData(
        val dateTime: String,
        val value: Int
)

@Serializable
data class FitbitSleepSummary(
        val stages: FitbitSleepStages? = null,
        val totalMinutesAsleep: Long,
        val totalSleepRecords: Long,
        val totalTimeInBed: Long
) {
    fun mapToOMH(sleepDate: LocalDate): List<OmhDTO> {
        val duration = DurationUnitValue(DurationUnit.MINUTE, totalMinutesAsleep)
        val timeIntervalDuration = DurationUnitValue(DurationUnit.DAY, 1)
        val startDateTime = sleepDate.atStartOfDay().atOffset(ZoneOffset.UTC)
        val timeFrame = TimeInterval.ofStartDateTimeAndDuration(startDateTime, timeIntervalDuration)
        val sleepDuration = SleepDuration2.Builder(duration, timeFrame).build()

        return listOf(OmhDTO(sleepDuration2 = sleepDuration))
    }
}

@Serializable
data class FitbitSleepStages(
        val deep: Long,
        val light: Long,
        val rem: Long,
        val wake: Long
)
