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
    override fun mapToOMH(parameters: Map<String,String>): OmhDTO {
        val fitbitUserId = parameters[FitbitConstants.UserParameterTag]
        val dateParameter = parameters[FitbitConstants.DateParameterTag]
        val date = if (dateParameter != null) LocalDate.parse(dateParameter) else LocalDate.now()
        val sleepEpisodes = sleep.map { it.mapToOMH() }
        val sleepDuration = summary.mapToOMH(date)

        return OmhDTO(fitbitUserId, date = date, sleepEpisodes = sleepEpisodes, sleepDuration2 = sleepDuration)
    }
}

@Serializable
data class FitbitSleepLog(
        val awakeCount: Long,
        val awakeDuration: Long,
        val awakeningsCount: Int,
        val infoCode: Int,
        @Serializable(with = LocalDateSerializer::class) val dateOfSleep: LocalDate,
        val duration: Long,
        val efficiency: Long,
        @Serializable(with = LocalDateTimeSerializer::class) val endTime: LocalDateTime,
        val isMainSleep: Boolean,
        val logId: Long,
        val levels: FitbitSleepLevels,
        val minutesAfterWakeup: Long,
        val minutesAsleep: Long,
        val minutesAwake: Long,
        val minutesToFallAsleep: Long,
        @Serializable(with = LocalDateTimeSerializer::class) val startTime: LocalDateTime,
        val timeInBed: Long,
        val type: String
) {
    fun mapToOMH(): SleepEpisode {
        val timeInterval = TimeInterval.ofStartDateTimeAndEndDateTime(
                startTime.atOffset(ZoneOffset.UTC), endTime.atOffset(ZoneOffset.UTC))
        val effectiveTimeFrame = TimeFrame(timeInterval)
        return SleepEpisode.Builder(effectiveTimeFrame)
                .setMainSleep(isMainSleep)
                .setNumberOfAwakenings(awakeningsCount)
                .setLatencyToSleepOnset(DurationUnitValue(DurationUnit.MINUTE, minutesToFallAsleep))
                .setTotalSleepTime(DurationUnitValue(DurationUnit.MINUTE, minutesAsleep))
                .setSleepMaintenanceEfficiencyPercentage(TypedUnitValue(PercentUnit.PERCENT, efficiency))
                .setLatencyToArising(DurationUnitValue(DurationUnit.MINUTE, minutesAfterWakeup))
                .build()
    }
}

@Serializable
data class FitbitSleepLevels(
        val data: List<FitbitSleepLongData>,
        val shortData: List<FitbitSleepShortData>,
        val summary: FitbitSleepLevelsFullSummary? = null
)

@Serializable
data class FitbitSleepShortData(
        val dateTime: String,
        val level: String,
        val seconds: Int
)

@Serializable
data class FitbitSleepLongData(
        val dateTime: String,
        val level: String,
        val seconds: Int
)

@Serializable
data class FitbitSleepLevelsFullSummary(
        val deep: FitbitSleepLevelsSummary,
        val light: FitbitSleepLevelsSummary,
        val rem: FitbitSleepLevelsSummary,
        val wake: FitbitSleepLevelsSummary
)

@Serializable
data class FitbitSleepLevelsSummary(
        val count: Int,
        val minutes: Int,
        val thirtyDayAvgMinutes: Int
)

@Serializable
data class FitbitSleepSummary(
        val stages: FitbitSleepStages? = null,
        val totalMinutesAsleep: Long,
        val totalSleepRecords: Int,
        val totalTimeInBed: Int
) {
    fun mapToOMH(sleepDate: LocalDate): SleepDuration2 {
        val duration = DurationUnitValue(DurationUnit.MINUTE, totalMinutesAsleep)
        val timeIntervalDuration = DurationUnitValue(DurationUnit.DAY, 1)
        val startDateTime = sleepDate.atStartOfDay().atOffset(ZoneOffset.UTC)
        val timeFrame = TimeInterval.ofStartDateTimeAndDuration(startDateTime, timeIntervalDuration)

        return SleepDuration2.Builder(duration, timeFrame).build()
    }
}

@Serializable
data class FitbitSleepStages(
        val deep: Int,
        val light: Int,
        val rem: Int,
        val wake: Int
)
