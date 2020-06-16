package dtu.openhealth.integration.fitbit.data.sleep

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.shared.dto.OmhDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class FitbitSleepMappingTest {
    private val sleepDate = LocalDate.of(2020,6,27)
    private val sleepDateString = sleepDate.toString()
    private val fitbitUserId = "hjdlafhska"
    private val awakeCount = 32

    @Test
    fun testFitbitSleepMapping()
    {
        val sleepLog = prepareSleepLog()
        val sleepSummary = FitbitSleepSummary(
                FitbitSleepStages(deep = 88, light = 266, rem = 92, wake = 61),
                totalMinutesAsleep = 448,
                totalSleepRecords = 1,
                totalTimeInBed = 507)
        val fitbitSleepLogSummary = FitbitSleepLogSummary(listOf(sleepLog), sleepSummary)
        val parameters = urlParameters()
        val omhDTO = fitbitSleepLogSummary.mapToOMH(parameters)
        assertThat(omhDTO.extUserId).isEqualTo(fitbitUserId)
        assertThat(omhDTO.date).isEqualTo(sleepDate)

        validateSleepEpisodes(omhDTO, sleepLog)
        validateSleepDuration(omhDTO, sleepSummary)
    }

    private fun validateSleepEpisodes(omhDTO: OmhDTO, sleepLog: FitbitSleepLog)
    {
        val sleepEpisodeList = omhDTO.sleepEpisodes
        val expectedElements = 1
        assertThat(sleepEpisodeList?.size).isEqualTo(expectedElements)

        val sleepEpisode = sleepEpisodeList?.get(0)
        val episodeTimeFrame = sleepEpisode?.effectiveTimeFrame
        assertThat(episodeTimeFrame?.timeInterval?.startDateTime).isEqualTo(sleepLog.startTime.atOffset(ZoneOffset.UTC))
        assertThat(episodeTimeFrame?.timeInterval?.endDateTime).isEqualTo(sleepLog.endTime.atOffset(ZoneOffset.UTC))
        assertThat(sleepEpisode?.mainSleep).isEqualTo(sleepLog.isMainSleep)
        assertThat(sleepEpisode?.numberOfAwakenings).isEqualTo(awakeCount)
        assertThat(sleepEpisode?.latencyToSleepOnset?.value?.longValueExact()).isEqualTo(sleepLog.minutesToFallAsleep)
        assertThat(sleepEpisode?.totalSleepTime?.value?.longValueExact()).isEqualTo(sleepLog.minutesAsleep)
        assertThat(sleepEpisode?.sleepMaintenanceEfficiencyPercentage?.value?.longValueExact()).isEqualTo(sleepLog.efficiency)
        assertThat(sleepEpisode?.latencyToArising?.value?.longValueExact()).isEqualTo(sleepLog.minutesAfterWakeup)
    }

    private fun validateSleepDuration(omhDTO: OmhDTO, sleepSummary: FitbitSleepSummary)
    {
        val sleepDuration = omhDTO.sleepDuration2

        assertThat(sleepDuration?.sleepDuration?.typedUnit).isEqualTo(DurationUnit.MINUTE)
        assertThat(sleepDuration?.sleepDuration?.value?.longValueExact()).isEqualTo(sleepSummary.totalMinutesAsleep)

        val durationInterval = sleepDuration?.effectiveTimeFrame
        val expectedStartDateTime = sleepDate.atStartOfDay().atOffset(ZoneOffset.UTC)
        assertThat(durationInterval?.timeInterval?.startDateTime).isEqualTo(expectedStartDateTime)
        assertThat(durationInterval?.timeInterval?.duration?.typedUnit).isEqualTo(DurationUnit.DAY)
        assertThat(durationInterval?.timeInterval?.duration?.value?.intValueExact()).isEqualTo(1)
    }

    private fun prepareSleepLog() : FitbitSleepLog
    {
        return FitbitSleepLog(
                dateOfSleep = sleepDate,
                duration = 30420000,
                efficiency = 96,
                endTime = LocalDateTime.of(sleepDate, LocalTime.of(7, 13, 30)),
                isMainSleep = true,
                logId = 26454176508,
                levels = sleepLevels(),
                minutesAfterWakeup = 2,
                minutesAsleep = 488,
                minutesAwake = 19,
                minutesToFallAsleep = 11,
                startTime = LocalDateTime.of(LocalDate.of(2020, 6, 26), LocalTime.of(22, 46, 30)),
                timeInBed = 507,
                type = "stages",
                infoCode = 0
        )
    }

    private fun sleepLevels() : FitbitSleepLevels
    {
        return FitbitSleepLevels(
                sleepLongData(),
                sleepShortData(),
                summary = FitbitSleepLevelsFullSummary(
                        deep = FitbitSleepLevelsSummary(
                                count = 5,
                                minutes = 61,
                                thirtyDayAvgMinutes = 58),
                        light = FitbitSleepLevelsSummary(
                                count = 9,
                                minutes = 89,
                                thirtyDayAvgMinutes = 78),
                        rem = FitbitSleepLevelsSummary(
                                count = 7,
                                minutes = 61,
                                thirtyDayAvgMinutes = 58),
                        wake = FitbitSleepLevelsSummary(
                                count = awakeCount,
                                minutes = 48,
                                thirtyDayAvgMinutes = 54)
                )
        )
    }

    private fun sleepLongData(): List<FitbitSleepLongData>
    {
        return listOf(FitbitSleepLongData("10-12-1999", "deep", 160))
    }

    private fun sleepShortData(): List<FitbitSleepShortData>
    {
        return listOf(FitbitSleepShortData("10-12-1999", "deep", 160))
    }

    private fun urlParameters(): Map<String,String>
    {
        return mapOf(
                Pair(FitbitConstants.UserParameterTag, fitbitUserId),
                Pair(FitbitConstants.DateParameterTag, sleepDateString)
        )
    }
}
